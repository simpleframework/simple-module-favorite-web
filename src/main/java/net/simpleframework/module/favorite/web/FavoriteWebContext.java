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
	protected Module createModule() {
		return super.createModule().setDefaultFunction(
				new WebModuleFunction(FavoritesMgrPage.class)
						.setName(MODULE_NAME + "-FavoritesMgrPage").setText($m("FavoriteWebContext.1")));
	}

	public WebModuleFunction MY_FAVORITE_FUNCTION = (WebModuleFunction) new WebModuleFunction()
			.setUrl(getUrlsFactory().getMyFavoriteUrl(0)).setName(MODULE_NAME + "-MyFavoritesTPage")
			.setText($m("FavoriteWebContext.0")).setDisabled(true);

	@Override
	protected ModuleFunctions getFunctions() {
		return ModuleFunctions.of(MY_FAVORITE_FUNCTION);
	}

	@Override
	public AbstractElement<?> toMyFavoriteElement(final PageParameter pp) {
		return new LinkElement(MY_FAVORITE_FUNCTION.getText()).setHref(MY_FAVORITE_FUNCTION.getUrl());
	}
}
