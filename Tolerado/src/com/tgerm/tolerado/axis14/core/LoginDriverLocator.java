/*
Copyright (c) 2010 tgerm.com
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:

1. Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in the
   documentation and/or other materials provided with the distribution.
3. The name of the author may not be used to endorse or promote products
   derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE AUTHOR "AS IS" AND ANY EXPRESS OR
IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, 
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.tgerm.tolerado.axis14.core;

import com.tgerm.tolerado.common.ToleradoException;

public class LoginDriverLocator {
	private static final String CLS_PARTNER_DRIVER = "com.tgerm.tolerado.axis14.partner.PartnerLoginDriver";
	private static final String CLS_ENTERPRISE_DRIVER = "com.tgerm.tolerado.axis14.enterprise.EnterpriseLoginDriver";

	public static LoginDriver locate() {
		Class<?> driver = loadLoginDriver(CLS_PARTNER_DRIVER);
		if (driver == null) {
			driver = loadLoginDriver(CLS_ENTERPRISE_DRIVER);
		}
		try {
			return (LoginDriver) driver.newInstance();
		} catch (Exception e) {
			throw new ToleradoException("Failed to instantiate Login Driver", e);
		}
	}

	private static Class<?> loadLoginDriver(String driverClass) {
		Class<?> cls = null;
		try {
			cls = Class.forName(driverClass);
		} catch (ClassNotFoundException e) {
		}
		return cls;
	}

	public static void main(String[] args) {
		LoginDriver locateDriver = locate();
		System.out.println(locateDriver);
	}
}
