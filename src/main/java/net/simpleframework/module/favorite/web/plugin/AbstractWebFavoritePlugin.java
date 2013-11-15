package net.simpleframework.module.favorite.web.plugin;

import static net.simpleframework.common.I18n.$m;

import java.util.Date;

import net.simpleframework.common.Convert;
import net.simpleframework.common.ID;
import net.simpleframework.common.object.ObjectFactory;
import net.simpleframework.common.web.html.HtmlUtils;
import net.simpleframework.ctx.InjectCtx;
import net.simpleframework.ctx.permission.IPermissionConst;
import net.simpleframework.ctx.trans.Transaction;
import net.simpleframework.module.common.content.AbstractContentBean;
import net.simpleframework.module.common.plugin.AbstractModulePlugin;
import net.simpleframework.module.favorite.Favorite;
import net.simpleframework.module.favorite.FavoriteException;
import net.simpleframework.module.favorite.FavoriteItem;
import net.simpleframework.module.favorite.IFavoriteContent;
import net.simpleframework.module.favorite.IFavoriteService;
import net.simpleframework.module.favorite.plugin.IFavoriteContext;
import net.simpleframework.module.favorite.web.page.FavoritesExistPage;
import net.simpleframework.mvc.AbstractMVCPage;
import net.simpleframework.mvc.IForward;
import net.simpleframework.mvc.JavascriptForward;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.TextForward;
import net.simpleframework.mvc.common.element.AbstractElement;
import net.simpleframework.mvc.common.element.SpanElement;
import net.simpleframework.mvc.component.ComponentParameter;
import net.simpleframework.mvc.component.base.ajaxrequest.AjaxRequestBean;
import net.simpleframework.mvc.component.base.ajaxrequest.DefaultAjaxRequestHandler;
import net.simpleframework.mvc.component.ui.window.WindowBean;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class AbstractWebFavoritePlugin extends AbstractModulePlugin implements
		IWebFavoritePlugin {

	@InjectCtx
	protected static IFavoriteContext favoriteContext;

	protected void initComponents(final PageParameter pp, final Object contentId) {
		// 收藏
		pp.addComponentBean(COMPONENT_PREFIX + "Save", AjaxRequestBean.class)
				.setRole(IPermissionConst.ROLE_ALL_ACCOUNT).setHandleClass(AddtoFavoritesHandler.class);

		// 添加一个异常处理组件
		addExistWindowBean(pp);

		// ajax更新
		pp.addComponentBean(COMPONENT_PREFIX + "Update", AjaxRequestBean.class)
				.setUpdateContainerId("favorite_" + contentId).setHandleClass(FavoriteUpdate.class);
	}

	protected WindowBean addExistWindowBean(final PageParameter pp) {
		pp.addComponentBean(COMPONENT_PREFIX + "ExistPage", AjaxRequestBean.class).setUrlForward(
				AbstractMVCPage.url(FavoritesExistPage.class));
		return pp.addComponentBean(COMPONENT_PREFIX + "ExistWindow", WindowBean.class).setPopup(true)
				.setContentRef(COMPONENT_PREFIX + "ExistPage")
				.setTitle($m("AbstractWebFavoritePlugin.1")).setHeight(140).setWidth(360)
				.setXdelta(-120).setResizable(false);
	}

	@Override
	public AbstractElement<?> toFavoriteOpElement(final PageParameter pp, final Object contentId) {
		initComponents(pp, contentId);
		return new SpanElement(getFavoriteText(contentId))
				.setTitle($m("AbstractWebFavoritePlugin.0"));
	}

	protected String getFavoriteText(final Object contentId) {
		final StringBuilder sb = new StringBuilder();
		sb.append("<a onclick=\"").append(getFavoriteOnclick(contentId)).append("\">");
		sb.append($m("AbstractWebFavoritePlugin.0"));
		sb.append("</a>");
		sb.append("<span id='favorite_").append(contentId).append("'>");
		sb.append(toFavoritesNum(contentId));
		sb.append("</span>");
		return sb.toString();
	}

	protected String getFavoriteOnclick(final Object contentId) {
		final StringBuilder sb = new StringBuilder();
		sb.append("$Actions['").append(COMPONENT_PREFIX).append("Save']('favoriteMark=")
				.append(getMark()).append("&contentId=").append(contentId).append("&plugin=")
				.append(ObjectFactory.original(getClass()).getName()).append("');");
		return sb.toString();
	}

	protected String toFavoritesNum(final Object contentId) {
		final StringBuilder sb = new StringBuilder();
		final int favorites = getFavoritesNum(contentId);
		if (favorites > 0) {
			sb.append(" (").append(favorites).append(")");
		}
		return sb.toString();
	}

	protected int getFavoritesNum(final Object contentId) {
		final FavoriteItem favoriteItem = favoriteContext.getFavoriteService().getFavoriteItem(
				getMark(), contentId);
		return favoriteItem != null ? favoriteItem.getFavorites() : 0;
	}

	protected void doInsertFavorite(final PageParameter pp, final Object contentId) {
		final IFavoriteService service = favoriteContext.getFavoriteService();
		final Favorite favorite = service.createBean();
		final IFavoriteContent obj = getContent(contentId);
		favorite.setUserId(pp.getLoginId());
		favorite.setCreateDate(new Date());
		service.insertFavorite(getMark(), favorite, obj);
	}

	protected Favorite getMyFavorite(final PageParameter pp, final Object contentId) {
		return favoriteContext.getFavoriteService()
				.getFavorite(pp.getLoginId(), getMark(), contentId);
	}

	private static AbstractWebFavoritePlugin getPlugin(final PageParameter pp) {
		final int iMark = Convert.toInt(pp.getParameter("favoriteMark"));
		final AbstractWebFavoritePlugin plugin = (AbstractWebFavoritePlugin) favoriteContext
				.getPluginRegistry().getPlugin(iMark);
		if (plugin == null) {
			throw FavoriteException.of($m("AbstractWebFavoritePlugin.2", iMark));
		}
		return plugin;
	}

	public static class AddtoFavoritesHandler extends DefaultAjaxRequestHandler {

		@Transaction(context = IFavoriteContext.class)
		@Override
		public IForward ajaxProcess(final ComponentParameter cp) {
			final AbstractWebFavoritePlugin plugin = getPlugin(cp);
			final int iMark = plugin.getMark();
			final String contentId = cp.getParameter("contentId");

			final JavascriptForward js = new JavascriptForward();
			final Favorite favorite = plugin.getMyFavorite(cp, contentId);
			if (favorite != null) {
				js.append("var act=$Actions['").append(COMPONENT_PREFIX).append("ExistWindow'];");
				js.append("act.trigger=$Actions['").append(COMPONENT_PREFIX).append("Save'].trigger;");
				js.append("act('favoriteMark=").append(iMark).append("');");
				return js;
			}
			plugin.doInsertFavorite(cp, contentId);
			js.append("$Actions['").append(COMPONENT_PREFIX).append("Update']('favoriteMark=")
					.append(iMark).append("&contentId=").append(contentId).append("');");
			return js;
		}
	}

	public static class FavoriteUpdate extends DefaultAjaxRequestHandler {

		@Override
		public IForward ajaxProcess(final ComponentParameter cp) {
			final String contentId = cp.getParameter("contentId");
			return new TextForward(getPlugin(cp).toFavoritesNum(contentId));
		}
	}

	public static abstract class AbstractFavoriteContent implements IFavoriteContent {
		private final AbstractContentBean content;

		public AbstractFavoriteContent(final AbstractContentBean content) {
			this.content = content;
		}

		@Override
		public ID getContentId() {
			return content.getId();
		}

		@Override
		public String getTopic() {
			return content.getTopic();
		}

		@Override
		public String getDescription() {
			return HtmlUtils.truncateHtml(HtmlUtils.createHtmlDocument(content.getContent()), 128,
					"<br>", false, false);
		}
	}

	protected static final String COMPONENT_PREFIX = "AbstractWebFavoriteMark_";
}
