package net.simpleframework.module.favorite.web;

import net.simpleframework.common.web.HttpUtils;
import net.simpleframework.module.favorite.web.page.t1.FavoritesMgrPage;
import net.simpleframework.module.favorite.web.page.t2.MyFavoritesPage;
import net.simpleframework.mvc.AbstractMVCPage;
import net.simpleframework.mvc.common.UrlsCache;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public class FavoriteUrlsFactory extends UrlsCache {

	public String getFavoritesMgrUrl(final String params) {
		return AbstractMVCPage.url(FavoritesMgrPage.class, params);
	}

	public String getFavoritesMgrUrl() {
		return getFavoritesMgrUrl(null);
	}

	public String getMyFavoriteUrl(final int favoriteMark) {
		return getMyFavoriteUrl(favoriteMark, null);
	}

	protected Class<? extends AbstractMVCPage> getMyFavoritesPage() {
		return MyFavoritesPage.class;
	}

	public String getMyFavoriteUrl(final int favoriteMark, final String params) {
		String url = AbstractMVCPage.url(getMyFavoritesPage());
		if (favoriteMark != 0) {
			url += "?favoriteMark=" + favoriteMark;
		}
		return HttpUtils.addParameters(url, params);
	}
}
