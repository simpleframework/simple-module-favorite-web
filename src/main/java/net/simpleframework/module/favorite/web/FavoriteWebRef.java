package net.simpleframework.module.favorite.web;

import net.simpleframework.module.favorite.FavoriteRef;
import net.simpleframework.module.favorite.web.plugin.IWebFavoritePlugin;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.AbstractElement;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public abstract class FavoriteWebRef extends FavoriteRef {

	@Override
	public IWebFavoritePlugin plugin() {
		return (IWebFavoritePlugin) super.plugin();
	}

	public AbstractElement<?> toFavoriteElement(final PageParameter pp, final Object contentId) {
		return plugin().toFavoriteOpElement(pp, contentId);
	}
}
