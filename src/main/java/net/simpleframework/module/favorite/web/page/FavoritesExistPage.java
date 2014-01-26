package net.simpleframework.module.favorite.web.page;

import java.util.Map;

import net.simpleframework.common.Convert;
import net.simpleframework.common.coll.KVMap;
import net.simpleframework.module.favorite.IFavoriteContextAware;
import net.simpleframework.module.favorite.web.IFavoriteWebContext;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.template.AbstractTemplatePage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class FavoritesExistPage extends AbstractTemplatePage implements IFavoriteContextAware {

	@Override
	protected void onForward(final PageParameter pp) {
		super.onForward(pp);

		pp.addImportCSS(FavoritesExistPage.class, "/favorite.css");
	}

	@Override
	public Map<String, Object> createVariables(final PageParameter pp) {
		final int favoriteMark = Convert.toInt(pp.getParameter("favoriteMark"));
		return ((KVMap) super.createVariables(pp)).add(
				"myUrl",
				((IFavoriteWebContext) context).getUrlsFactory().getFavoriteUrl(pp,
						MyFavoritesTPage.class, favoriteMark)).add("times", "times_" + hashId);
	}
}
