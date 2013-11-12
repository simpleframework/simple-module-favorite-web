package net.simpleframework.module.favorite.web.page;

import static net.simpleframework.common.I18n.$m;

import java.util.Map;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.ctx.trans.Transaction;
import net.simpleframework.module.common.bean.CategoryStat;
import net.simpleframework.module.common.plugin.IModulePlugin;
import net.simpleframework.module.favorite.Favorite;
import net.simpleframework.module.favorite.FavoriteItem;
import net.simpleframework.module.favorite.IFavoriteContextAware;
import net.simpleframework.module.favorite.plugin.IFavoriteContext;
import net.simpleframework.module.favorite.plugin.IFavoritePlugin;
import net.simpleframework.module.favorite.web.IFavoriteWebContext;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.BlockElement;
import net.simpleframework.mvc.common.element.ButtonElement;
import net.simpleframework.mvc.common.element.EElementEvent;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.LinkElement;
import net.simpleframework.mvc.common.element.LinkElementEx;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.common.element.SupElement;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.mvc.component.ui.pager.db.AbstractDbTablePagerHandler;
import net.simpleframework.mvc.component.ui.tooltip.ETipElement;
import net.simpleframework.mvc.component.ui.tooltip.ETipPosition;
import net.simpleframework.mvc.component.ui.tooltip.ETipStyle;
import net.simpleframework.mvc.component.ui.tooltip.TipBean;
import net.simpleframework.mvc.component.ui.tooltip.TipBean.HideOn;
import net.simpleframework.mvc.component.ui.tooltip.TipBean.Hook;
import net.simpleframework.mvc.component.ui.tooltip.TooltipBean;
import net.simpleframework.mvc.template.lets.Category_ListPage;
import net.simpleframework.mvc.template.struct.CategoryItem;
import net.simpleframework.mvc.template.struct.CategoryItems;
import net.simpleframework.mvc.template.struct.NavigationButtons;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public class MyFavoritesTPage extends Category_ListPage implements IFavoriteContextAware {

	@Override
	protected void addComponents(final PageParameter pp) {
		super.addComponents(pp);

		final TablePagerBean tablePager = addTablePagerBean(pp, "MyFavoritesTPage_tbl",
				FavoriteList.class).setShowHead(true).setShowCheckbox(true);
		tablePager.addColumn(new TablePagerColumn("createDate", $m("MyFavoritesTPage.0"), 120))
				.addColumn(new TablePagerColumn("favorites", $m("MyFavoritesTPage.1"), 80))
				.addColumn(TablePagerColumn.OPE().setWidth(80))
				.setJsLoadedCallback("$Actions['MyFavoritesTPage_Tip']();");

		addAjaxRequest(pp, "MyFavoritesTPage_Cancel").setConfirmMessage($m("MyFavoritesTPage.3"))
				.setHandleMethod("doCancelFavorites");

		final TooltipBean tooltip = (TooltipBean) addComponentBean(pp, "MyFavoritesTPage_Tip",
				TooltipBean.class).setRunImmediately(false);
		tooltip.addTip(new TipBean(tooltip).setSelector("#" + tablePager.getContainerId() + " a")
				.setDelay(0.5).setTipStyle(ETipStyle.tipDarkgrey).setStem(ETipPosition.rightTop)
				.setHook(new Hook(ETipPosition.leftTop, ETipPosition.rightTop))
				.setHideOn(new HideOn(ETipElement.tip, EElementEvent.mouseleave)).setWidth(320));
	}

	@Transaction(context = IFavoriteContext.class)
	public IForward doCancelFavorites(final ComponentParameter cp) {
		context.getFavoriteService().delete(cp.getParameter("id"));
		return new JavascriptForward("$Actions['MyFavoritesTPage_tbl']();");
	}

	@Override
	protected CategoryItems getCategoryList(final PageParameter pp) {
		final IFavoritePlugin favoriteMark = getFavoritePlugin(pp);
		final CategoryItems blocks = CategoryItems.of();
		for (final IModulePlugin tMark : context.getPluginRegistry().allPlugin()) {
			final String url = ((IFavoriteWebContext) context).getUrlsFactory().getMyFavoriteUrl(
					tMark.getMark());
			final CategoryItem block = new CategoryItem(tMark.getText()).setHref(url);
			block.setSelected(favoriteMark != null && favoriteMark.getMark() == tMark.getMark());
			final int num = context.getFavoriteService()
					.queryFavorites(tMark.getMark(), pp.getLoginId(), null).getCount();
			if (num > 0) {
				block.setNum(new SupElement(num));
			}
			blocks.add(block);
		}
		return blocks;
	}

	@Override
	public ElementList getLeftElements(final PageParameter pp) {
		final ElementList el = ElementList.of();
		final IFavoritePlugin favoritePlugin = getFavoritePlugin(pp);
		if (favoritePlugin != null) {
			final String categoryId = pp.getParameter("categoryId");
			final int iMark = favoritePlugin.getMark();
			for (final CategoryStat stat : context.getFavoriteService().queryCategoryItems(iMark,
					pp.getLoginId())) {
				final Object categoryId2 = stat.getCategoryId();
				final LinkElement link = new LinkElementEx(StringUtils.text(
						favoritePlugin.getCategoryText(categoryId2), $m("MyFavoritesTPage.4")))
						.setSelected(categoryId2.equals(categoryId)).setHref(
								((IFavoriteWebContext) context).getUrlsFactory().getMyFavoriteUrl(iMark,
										"categoryId=" + categoryId2));
				el.append(link,
						new SupElement("(" + stat.getCount() + ")").setStyle("margin-left: 4px;"),
						SpanElement.SPACE);
			}
		}
		return el;
	}

	@Override
	public NavigationButtons getNavigationBar(final PageParameter pp) {
		final NavigationButtons btns = NavigationButtons.of();
		final IFavoritePlugin favoriteMark = MyFavoritesTPage.getFavoritePlugin(pp);
		final String txt = $m("FavoriteWebContext.0");
		btns.append(favoriteMark != null ? new LinkElement(txt)
				.setHref(((IFavoriteWebContext) context).getUrlsFactory().getMyFavoriteUrl(0))
				: new SpanElement(txt));
		if (favoriteMark != null) {
			btns.add(new SpanElement(favoriteMark.getText()));
		}
		return btns;
	}

	public static IFavoritePlugin getFavoritePlugin(final PageParameter pp) {
		return context.getPluginRegistry().getPlugin(pp.getIntParameter("favoriteMark"));
	}

	public static class FavoriteList extends AbstractDbTablePagerHandler {
		@Override
		public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
			final IFavoritePlugin favoriteMark = getFavoritePlugin(cp);
			if (favoriteMark != null) {
				cp.addFormParameter("favoriteMark", favoriteMark.getMark());
			}
			final String categoryId = cp.getParameter("categoryId");
			if (StringUtils.hasText(categoryId)) {
				cp.addFormParameter("categoryId", categoryId);
			}
			return context.getFavoriteService().queryFavorites(
					favoriteMark != null ? favoriteMark.getMark() : 0, cp.getLoginId(), categoryId);
		}

		@Override
		protected Map<String, Object> getRowData(final ComponentParameter cp, final Object dataObject) {
			final KVMap kv = new KVMap();
			final Favorite favorite = (Favorite) dataObject;
			final FavoriteItem favoriteItem = context.getFavoriteService().getFavoriteItem(favorite);
			if (favoriteItem != null) {
				final StringBuilder sb = new StringBuilder();
				sb.append("<a target='_blank' href='").append(favoriteItem.getUrl()).append("'>")
						.append(favoriteItem.getTopic()).append("</a>");
				final String desc = favoriteItem.getDescription();
				if (StringUtils.hasText(desc)) {
					sb.append(BlockElement.tip(desc));
				}
				kv.put("topic", sb.toString());
				kv.put("favorites", favoriteItem.getFavorites());
			}
			kv.put("createDate", favorite.getCreateDate());
			kv.put(TablePagerColumn.OPE, new ButtonElement($m("MyFavoritesTPage.2"))
					.setOnclick("$Actions['MyFavoritesTPage_Cancel']('id=" + favorite.getId() + "');"));
			return kv;
		}
	}
}
