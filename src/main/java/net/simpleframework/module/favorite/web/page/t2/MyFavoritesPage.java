package net.simpleframework.module.favorite.web.page.t2;

import java.io.IOException;
import java.util.Map;

import net.simpleframework.ctx.permission.PermissionConst;
import net.simpleframework.module.favorite.IFavoriteContextAware;
import net.simpleframework.module.favorite.web.page.MyFavoritesTPage;
import net.simpleframework.mvc.PageMapping;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.template.struct.NavigationButtons;
import net.simpleframework.mvc.template.t2.T2TemplatePage;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
@PageMapping(url = "/favorites/my")
public class MyFavoritesPage extends T2TemplatePage implements IFavoriteContextAware {

	@Override
	protected String toHtml(final PageParameter pp, final Map<String, Object> variables,
			final String currentVariable) throws IOException {
		return pp.includeUrl(MyFavoritesTPage.class);
	}

	@Override
	public NavigationButtons getNavigationBar(final PageParameter pp) {
		return super.getNavigationBar(pp).appendAll(
				singleton(MyFavoritesTPage.class).getNavigationBar(pp));
	}

	@Override
	public String getPageRole(final PageParameter pp) {
		return PermissionConst.ROLE_ALL_ACCOUNT;
	}
}