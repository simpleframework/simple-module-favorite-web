package net.simpleframework.module.favorite.web;

import net.simpleframework.common.StringUtils;
import net.simpleframework.module.favorite.web.page.MyFavoritesTPage;
import net.simpleframework.module.favorite.web.page.t1.FavoritesMgrPage;
import net.simpleframework.module.favorite.web.page.t2.MyFavoritesPage;
import net.simpleframework.mvc.AbstractMVCPage;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.UrlsCache;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class FavoriteUrlsFactory extends UrlsCache {

	public FavoriteUrlsFactory() {
		put(MyFavoritesTPage.class, MyFavoritesPage.class);

		put(FavoritesMgrPage.class);
	}

	public String getFavoriteUrl(final PageParameter pp,
			final Class<? extends AbstractMVCPage> mClass, final int favoriteMark) {
		return getFavoriteUrl(pp, mClass, favoriteMark, null);
	}

	public String getFavoriteUrl(final PageParameter pp,
			final Class<? extends AbstractMVCPage> mClass, final int favoriteMark,
			final String params) {
		return getUrl(pp, mClass, StringUtils.join(
				new String[] { favoriteMark == 0 ? null : "favoriteMark=" + favoriteMark, params },
				"&"));
	}
}
