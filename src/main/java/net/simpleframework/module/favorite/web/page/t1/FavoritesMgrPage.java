package net.simpleframework.module.favorite.web.page.t1;

import static net.simpleframework.common.I18n.$m;

import java.io.IOException;
import java.util.Map;

import net.simpleframework.ado.query.IDataQuery;
import net.simpleframework.common.StringUtils;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.ctx.trans.Transaction;
import net.simpleframework.module.common.plugin.IModulePlugin;
import net.simpleframework.module.favorite.Favorite;
import net.simpleframework.module.favorite.FavoriteItem;
import net.simpleframework.module.favorite.IFavoriteContextAware;
import net.simpleframework.module.favorite.plugin.IFavoriteContext;
import net.simpleframework.module.favorite.plugin.IFavoritePlugin;
import net.simpleframework.module.favorite.web.FavoriteUrlsFactory;
import net.simpleframework.module.favorite.web.IFavoriteWebContext;
import net.simpleframework.module.favorite.web.page.FavoritesExistPage;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageMapping;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.ButtonElement;
import net.simpleframework.mvc.common.element.ElementList;
import net.simpleframework.mvc.common.element.InputElement;
import net.simpleframework.mvc.common.element.LinkButton;
import net.simpleframework.mvc.common.element.LinkElement;
import net.simpleframework.mvc.common.element.Option;
import net.simpleframework.mvc.common.element.TabButton;
import net.simpleframework.mvc.common.element.TabButtons;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.ui.pager.EPagerBarLayout;
import net.simpleframework.mvc.component.ui.pager.TablePagerBean;
import net.simpleframework.mvc.component.ui.pager.TablePagerColumn;
import net.simpleframework.mvc.component.ui.pager.db.AbstractDbTablePagerHandler;
import net.simpleframework.mvc.template.TemplateUtils;
import net.simpleframework.mvc.template.t1.T1ResizedTemplatePage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
@PageMapping(url = "/favorites/mgr")
public class FavoritesMgrPage extends T1ResizedTemplatePage implements IFavoriteContextAware {

	@Override
	protected void onForward(final PageParameter pp) throws Exception {
		super.onForward(pp);

		pp.addImportCSS(FavoritesExistPage.class, "/favorite.css");

		final TablePagerBean tablePager = (TablePagerBean) addComponentBean(pp, "tpFavoritesList",
				TablePagerBean.class).setShowLineNo(true).setPagerBarLayout(EPagerBarLayout.bottom)
				.setContainerId("list_" + hashId).setHandlerClass(FavoritesList.class);
		tablePager
				.addColumn(new TablePagerColumn("topic", $m("FavoritesMgrPage.0")))
				.addColumn(
						new TablePagerColumn("favorites", $m("FavoritesMgrPage.4"), 80)
								.setPropertyClass(Integer.class))
				.addColumn(createUserColumn(pp, "userId", $m("FavoritesMgrPage.1"), "tpFavoritesList"))
				.addColumn(TablePagerColumn.DATE("createDate", $m("FavoritesMgrPage.2")))
				.addColumn(TablePagerColumn.OPE(80));

		// delete
		addDeleteAjaxRequest(pp, "FavoritesMgrPage_delete");

		// 为表格过滤用的用户选择
		addUserSelectForTbl(pp, "tpFavoritesList");
	}

	@Override
	public String getPageRole(final PageParameter pp) {
		return favoriteContext.getModule().getManagerRole();
	}

	@Override
	public Map<String, Object> createVariables(final PageParameter pp) {
		return ((KVMap) super.createVariables(pp)).add("listId", "list_" + hashId);
	}

	@Transaction(context = IFavoriteContext.class)
	public IForward doDelete(final ComponentParameter cp) {
		final Object[] ids = StringUtils.split(cp.getParameter("id"));
		favoriteContext.getFavoriteService().delete(ids);
		final JavascriptForward js = new JavascriptForward("$Actions['tpFavoritesList']();");
		return js;
	}

	private static IFavoritePlugin getFavoriteMark(final PageParameter pp) {
		return favoriteContext.getPluginRegistry().getPlugin(pp.getIntParameter("favoriteMark"));
	}

	@Override
	public ElementList getLeftElements(final PageParameter pp) {
		final ElementList btns = ElementList.of(LinkButton.deleteBtn().setOnclick(
				"$Actions['tpFavoritesList'].doAct('FavoritesMgrPage_delete');"));
		return btns;
	}

	@Override
	public TabButtons getTabButtons(final PageParameter pp) {
		return TabButtons.of(new TabButton($m("FavoriteWebContext.1"),
				((IFavoriteWebContext) favoriteContext).getUrlsFactory().getUrl(pp,
						FavoritesMgrPage.class)));
	}

	@Override
	public ElementList getRightElements(final PageParameter pp) {
		final FavoriteUrlsFactory uFactory = ((IFavoriteWebContext) favoriteContext).getUrlsFactory();
		final InputElement select = InputElement.select().setOnchange(
				"$Actions.loc('" + uFactory.getUrl(pp, FavoritesMgrPage.class)
						+ "?favoriteMark=' + $F(this));");
		for (final IModulePlugin mark : favoriteContext.getPluginRegistry().allPlugin()) {
			final int iMark = mark.getMark();
			select.addElements(new Option(iMark, mark.getText()).setSelected(iMark == pp
					.getIntParameter("favoriteMark")));
		}
		return ElementList.of(select);
	}

	@Override
	protected String toHtml(final PageParameter pp, final Map<String, Object> variables,
			final String currentVariable) throws IOException {
		final StringBuilder sb = new StringBuilder();
		sb.append("<div class='FavoritesMgrPage'>");
		sb.append(" <div id='list_").append(hashId).append("'></div>");
		sb.append("</div>");
		return sb.toString();
	}

	public static class FavoritesList extends AbstractDbTablePagerHandler {
		@Override
		public IDataQuery<?> createDataObjectQuery(final ComponentParameter cp) {
			final IFavoritePlugin oMark = getFavoriteMark(cp);
			if (oMark != null) {
				cp.addFormParameter("favoriteMark", oMark.getMark());
			}
			return favoriteContext.getFavoriteService().queryFavorites(oMark.getMark(), null, null);
		}

		@Override
		protected Map<String, Object> getRowData(final ComponentParameter cp, final Object dataObject) {
			final KVMap kv = new KVMap();
			final Favorite favorite = (Favorite) dataObject;
			final FavoriteItem item = favoriteContext.getFavoriteService().getFavoriteItem(favorite);
			if (item != null) {
				kv.put("topic",
						new LinkElement(item.getTopic()).setTarget("_blank").setHref(item.getUrl()));
			}
			kv.add("favorites", item.getFavorites());
			kv.put("userId", TemplateUtils.toIconUser(cp, favorite.getUserId()));
			kv.put("createDate", favorite.getCreateDate());
			kv.put(
					TablePagerColumn.OPE,
					ButtonElement.deleteBtn().setOnclick(
							"$Actions['FavoritesMgrPage_delete']('id=" + favorite.getId() + "');"));
			return kv;
		}
	}
}
