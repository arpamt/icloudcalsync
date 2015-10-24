/*
 * Copyright © 2013 – 2015 Ricki Hirner (bitfire web engineering).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */
package com.granita.icloudcalsync;

import net.fortuna.ical4j.model.property.ProdId;

public class Constants {
	public static final String
		APP_VERSION = "0.8.0",
		ACCOUNT_TYPE = "com.granita.icloudcalsync",
		WEB_URL_HELP = "",
		WEB_URL_VIEW_LOGS = "";

	public static final ProdId ICAL_PRODID = new ProdId("-//bitfire web engineering//DAVdroid " + Constants.APP_VERSION + " (ical4j 1.0.x)//EN");
}
