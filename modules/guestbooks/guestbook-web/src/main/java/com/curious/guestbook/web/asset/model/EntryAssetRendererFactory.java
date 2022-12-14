package com.curious.guestbook.web.asset.model;

import com.liferay.asset.display.page.portlet.AssetDisplayPageFriendlyURLProvider;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.model.BaseAssetRendererFactory;
import com.curious.guestbook.constants.GuestbookPortletKeys;
import com.curious.guestbook.model.Entry;
import com.curious.guestbook.service.EntryLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;
import javax.portlet.WindowState;
import javax.portlet.WindowStateException;
import javax.servlet.ServletContext;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(immediate = true, property = {
		"javax.portlet.name=" + GuestbookPortletKeys.GUESTBOOK }, service = AssetRendererFactory.class)
public class EntryAssetRendererFactory extends BaseAssetRendererFactory<Entry> {

	public EntryAssetRendererFactory() {
		setClassName(CLASS_NAME);
		setLinkable(_LINKABLE);
		setPortletId(GuestbookPortletKeys.GUESTBOOK);
		setSearchable(true);
		setSelectable(true);

	}

	@Override
	public AssetRenderer<Entry> getAssetRenderer(long classPK, int type) throws PortalException {

		Entry entry = _entryLocalService.getEntry(classPK);

		EntryAssetRenderer entryAssetRenderer = new EntryAssetRenderer(entry);
		entryAssetRenderer.setAssetDisplayPageFriendlyURLProvider(_assetDisplayPageFriendlyURLProvider);
		entryAssetRenderer.setAssetRendererType(type);
		entryAssetRenderer.setServletContext(_servletContext);

		return entryAssetRenderer;
	}

	@Override
	public String getClassName() {
		return CLASS_NAME;
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public boolean hasPermission(PermissionChecker permissionChecker, long classPK, String actionId) throws Exception {
		return _entryModelResourcePermission.contains(permissionChecker, classPK, actionId);
	}

//	@Override
//	public boolean hasAddPermission(PermissionChecker permissionChecker, long groupId, long classTypeId)
//			throws Exception {
//
//		return _entryModelResourcePermission.contains(permissionChecker, groupId, ActionKeys.ADD_ENTRY);
//	}

	@Override
	public PortletURL getURLAdd(LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse, long classTypeId) {

		PortletURL portletURL = null;

		try {
			ThemeDisplay themeDisplay = (ThemeDisplay) liferayPortletRequest.getAttribute(WebKeys.THEME_DISPLAY);

			portletURL = liferayPortletResponse.createLiferayPortletURL(getControlPanelPlid(themeDisplay),
					GuestbookPortletKeys.GUESTBOOK, PortletRequest.RENDER_PHASE);
			portletURL.setParameter("mvcRenderCommandName", "/guestbookwebportlet/edit_entry");
			portletURL.setParameter("showback", Boolean.FALSE.toString());
		} catch (PortalException e) {
		}

		return portletURL;
	}

	@Override
	public PortletURL getURLView(LiferayPortletResponse liferayPortletResponse, WindowState windowState) {

		LiferayPortletURL liferayPortletURL = liferayPortletResponse
				.createLiferayPortletURL(GuestbookPortletKeys.GUESTBOOK, PortletRequest.RENDER_PHASE);

		try {
			liferayPortletURL.setWindowState(windowState);
		} catch (WindowStateException wse) {

		}
		return liferayPortletURL;
	}

	@Override
	public boolean isLinkable() {
		return _LINKABLE;
	}

	@Override
	public String getIconCssClass() {
		return "pencil";
	}

	@Reference
	private AssetDisplayPageFriendlyURLProvider _assetDisplayPageFriendlyURLProvider;

	@Reference(unbind = "-")
	protected void setEntryLocalService(EntryLocalService entryLocalService) {
		_entryLocalService = entryLocalService;
	}

	private EntryLocalService _entryLocalService;

	@Reference(target = "(osgi.web.symbolicname=com.curious)")
	private ServletContext _servletContext;

	private static final boolean _LINKABLE = true;
	public static final String CLASS_NAME = Entry.class.getName();
	public static final String TYPE = "entry";

	@Reference(target = "(model.class.name=com.curious.guestbook.model.Entry)", unbind = "-")
	private ModelResourcePermission<Entry> _entryModelResourcePermission;

}
