package net.simpleframework.module.favorite.web;

import static net.simpleframework.common.I18n.$m;
import net.simpleframework.ctx.Module;
import net.simpleframework.ctx.ModuleFunctions;
import net.simpleframework.module.favorite.impl.FavoriteContext;
import net.simpleframework.module.favorite.web.page.t1.FavoritesMgrPage;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.AbstractElement;
import net.simpleframework.mvc.common.element.LinkElement;
import net.simpleframework.mvc.ctx.WebModuleFunction;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public class FavoriteWebContext extends FavoriteContext implements IFavoriteWebContext {

	@Override
	public FavoriteUrlsFactory getUrlsFactory() {
		return singleton(FavoriteUrlsFactory.class);
	}

	@Override
	protected Module createModule() {
		return super.createModule().setDefaultFunction(FUNC_FAVORITES_MGR);
	}

	@Override
	protected ModuleFunctions getFunctions() {
		return ModuleFunctions.of(FUNC_FAVORITES_MGR, FUNC_MY_FAVORITES);
	}

	@Override
	public AbstractElement<?> toMyFavoriteElement(final PageParameter pp) {
		return new LinkElement(FUNC_MY_FAVORITES.getText()).setHref(FUNC_MY_FAVORITES.getUrl());
	}

	public WebModuleFunction FUNC_FAVORITES_MGR = (WebModuleFunction) new WebModuleFunction(
			FavoritesMgrPage.class).setName(MODULE_NAME + "-FavoritesMgrPage").setText(
			$m("FavoriteWebContext.1"));
	public WebModuleFunction FUNC_MY_FAVORITES = (WebModuleFunction) new WebModuleFunction()
			.setUrl(getUrlsFactory().getMyFavoriteUrl(0)).setName(MODULE_NAME + "-MyFavoritesTPage")
			.setText($m("FavoriteWebContext.0")).setDisabled(true);
}