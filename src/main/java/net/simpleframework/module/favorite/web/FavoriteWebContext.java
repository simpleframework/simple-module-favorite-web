package net.simpleframework.module.favorite.web;

import static net.simpleframework.common.I18n.$m;

import net.simpleframework.ctx.ModuleFunctions;
import net.simpleframework.module.favorite.impl.FavoriteContext;
import net.simpleframework.module.favorite.web.page.MyFavoritesTPage;
import net.simpleframework.module.favorite.web.page.t1.FavoritesMgrPage;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.AbstractElement;
import net.simpleframework.mvc.common.element.LinkElement;
import net.simpleframework.mvc.ctx.WebModuleFunction;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class FavoriteWebContext extends FavoriteContext implements IFavoriteWebContext {

	@Override
	public FavoriteUrlsFactory getUrlsFactory() {
		return singleton(FavoriteUrlsFactory.class);
	}

	@Override
	protected ModuleFunctions getFunctions() {
		return ModuleFunctions.of(
				new WebModuleFunction(this, FavoritesMgrPage.class)
						.setName(MODULE_NAME + "-FavoritesMgrPage").setText($m("FavoriteWebContext.0")),
				new WebModuleFunction(this)
						.setUrl(getUrlsFactory().getFavoriteUrl(null, MyFavoritesTPage.class, 0))
						.setName(MODULE_NAME + "-MyFavoritesTPage").setText($m("FavoriteContext.0"))
						.setDisabled(true));
	}

	@Override
	public AbstractElement<?> toMyFavoriteElement(final PageParameter pp) {
		final WebModuleFunction f = (WebModuleFunction) getFunctionByName(
				MODULE_NAME + "-MyFavoritesTPage");
		return new LinkElement(f.getText()).setHref(f.getUrl());
	}
}
