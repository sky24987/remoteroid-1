/*
 * Remoteroid - A remote control solution for Android platform, including handy file transfer and notify-to-PC.
 * Copyright (C) 2012 Taeho Kim(jyte82@gmail.com), Hyomin Oh(ohmnia1112@gmail.com), Hongkyun Kim(godgjdgjd@nate.com), Yongwan Hwang(singerhwang@gmail.com)
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package org.secmem.remoteroid.util;

import java.util.List;

import org.secmem.remoteroid.lib.data.Account;
import org.secmem.remoteroid.service.RemoteroidService;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;

public class Util {
	private static final boolean D = true;
	private static final String TAG = "RemoteroidUtil";
	
	public static class Services{
		/**
		 * Class name for Remoteroid NotificationReceiverService.
		 * @see org.secmem.remoteroid.service.NotificationReceiverService NotificationReceiverService
		 */
		private static final String ACC_SERVICE_NAME = "org.secmem.remoteroid/org.secmem.remoteroid.service.NotificationReceiverService";
		
		/**
		 * Determine whether Notification receiver service enabled or not.
		 * @param context Application or Activity's context
		 * @return true if Accessibility service has enabled, false otherwise
		 */
		public static boolean isAccessibilityServiceEnabled(Context context){
			try {
				boolean globalAccServiceEnabled = Secure.getInt(context.getContentResolver(), Secure.ACCESSIBILITY_ENABLED)==1?true:false;
				if(!globalAccServiceEnabled)
					return false;
				
				String enabledAccServices = Secure.getString(context.getContentResolver(), Secure.ENABLED_ACCESSIBILITY_SERVICES);
				return enabledAccServices.contains(ACC_SERVICE_NAME);
			} catch (SettingNotFoundException e) {
				e.printStackTrace();
			}
			return false;
		}
		
		/**
		 * Launch Accessibility settings activity.
		 * @param context Application or Activity's context
		 */
		public static void launchAccessibilitySettings(Context context){
			context.startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
		}
		
		/**
		 * Check RemoteroidService is running or not.
		 * @param context Application/Activity's context
		 * @return <code>true</code> if RemoteroidService is running, <code>false</code> otherwise.
		 */
		public static boolean isServiceAlive(Context context){
			String serviceCls = RemoteroidService.class.getName();
			ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
			List<RunningServiceInfo> serviceList = manager.getRunningServices(Integer.MAX_VALUE);
			int count = serviceList.size();
			for(int i=0; i<count; i++){
				RunningServiceInfo obj = serviceList.get(i);
				if(obj.service.getClassName().equals(serviceCls)){
					return true;
				}
			}
			if(D) Log.d(TAG, "RemoteroidService not available.");
			return false;
		}
		
		/**
		 * Starts RemoteroidService.
		 * @param context Application/Activity's context
		 * @see org.secmem.remoteroid.service.RemoteroidService RemoteroidService
		 */
		public static void startRemoteroidService(Context context){
			if(!isServiceAlive(context)){
				if(D) Log.d(TAG, "Starting RemoteroidService..");
				Intent intent = new Intent(context, RemoteroidService.class);
				context.startService(intent);
			}
		}
	}
	
	
	
	public static class Connection{
		private static final String KEY_ACCOUNT_ENABLED = "use_account";
		private static final String KEY_USER_EMAIL="user_email";
		private static final String KEY_PASSWORD = "password";
		@Deprecated
		private static final String KEY_AUTO_CONNECT = "auto_connect";
		private static final String KEY_SERVER_TYPE = "server_type";
		
		public static void setUserAccountEnabled(Context context, boolean enabled){
			SharedPreferences.Editor editor = getPrefEditor(context);
			editor.putBoolean(KEY_ACCOUNT_ENABLED, enabled);
			editor.putString(KEY_USER_EMAIL, null);
			editor.putString(KEY_PASSWORD, null);
			editor.commit();
		}
		
		public static void saveAuthData(Context context, String userEmail, String password){
			SharedPreferences.Editor editor = getPrefEditor(context);
			editor.putBoolean(KEY_ACCOUNT_ENABLED, true);
			editor.putString(KEY_USER_EMAIL, userEmail);
			editor.putString(KEY_PASSWORD, password);
			editor.commit();
		}
		
		@Deprecated
		public static void setAutoConnect(Context context, boolean autoconnect){
			SharedPreferences.Editor editor = getPrefEditor(context);
			editor.putBoolean(KEY_AUTO_CONNECT, autoconnect);
			editor.commit();
		}
		
		public static String getUserEmail(Context context){
			return getPref(context).getString(KEY_USER_EMAIL, null);
		}
		
		public static String getPassword(Context context){
			return getPref(context).getString(KEY_PASSWORD, null);
		}
		
		public static Account getUserAccount(Context context){
			Account account = new Account();
			account.setEmail(getUserEmail(context));
			account.setPassword(getPassword(context));
			return account;
		}
		
		public static boolean isUserAccountSet(Context context){
			return getPref(context).getBoolean(KEY_ACCOUNT_ENABLED, false);
		}
		
		@Deprecated
		public static boolean isAutoConnectEnabled(Context context){
			return getPref(context).getBoolean(KEY_AUTO_CONNECT, false);
		}
		
		public static String getServerType(Context context){
			return getPref(context).getString(KEY_SERVER_TYPE, "windows");
		}
	}
	
	public static class Filter{
		private static final String KEY_FILTERING_MODE = "filtering_mode";
		private static final String KEY_FILTER_ENABLED = "filter_enabled";
		private static final String KEY_NOTIFICATION_TYPE = "notification_type";
		
		public enum FilterMode{EXCLUDE, INCLUDE};
		public enum NotificationType{ALL, STATUSBAR, TOAST};
		
		public static FilterMode getFilterMode(Context context){
			String mode = getPref(context).getString(KEY_FILTERING_MODE, "e");
			if(mode.equals("e"))
				return FilterMode.EXCLUDE;
			else
				return FilterMode.INCLUDE;
		}
		
		public static boolean isFilterEnabled(Context context){
			return getPref(context).getBoolean(KEY_FILTER_ENABLED, false);
		}
		
		public static NotificationType getNotificationType(Context context){
			String mode = getPref(context).getString(KEY_NOTIFICATION_TYPE, "a");
			if(mode.equals("a"))
				return NotificationType.ALL;
			else if(mode.equals("n"))
				return NotificationType.STATUSBAR;
			else
				return NotificationType.TOAST;
		}
		
		
	}
	
	private static SharedPreferences getPref(Context context){
		return PreferenceManager.getDefaultSharedPreferences(context);
	}
	
	private static SharedPreferences.Editor getPrefEditor(Context context){
		return getPref(context).edit();
	}
}
