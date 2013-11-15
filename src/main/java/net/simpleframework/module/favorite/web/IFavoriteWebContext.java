package net.simpleframework.module.favorite.web;

import net.simpleframework.module.favorite.plugin.IFavoriteContext;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.AbstractElement;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public interface IFavoriteWebContext extends IFavoriteContext {

	/**
	 * 定义我的搜藏html元素
	 * 
	 * @param pp
	 * @return
	 */
	AbstractElement<?> toMyFavoriteElement(PageParameter pp);

	FavoriteUrlsFactory getUrlsFactory();
}
