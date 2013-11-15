package net.simpleframework.module.favorite.web.plugin;

import net.simpleframework.module.favorite.plugin.IFavoritePlugin;
import net.simpleframework.mvc.PageParameter;
import net.simpleframework.mvc.common.element.AbstractElement;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
public interface IWebFavoritePlugin extends IFavoritePlugin {

	/**
	 * 获取收藏的html元素
	 * 
	 * @param pp
	 * @param contentId
	 * @return
	 */
	AbstractElement<?> toFavoriteOpElement(PageParameter pp, Object contentId);
}
