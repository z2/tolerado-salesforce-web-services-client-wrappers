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

package com.tgerm.tolerado.wsc.core.method;

import com.tgerm.tolerado.wsc.core.ToleradoException;
import com.tgerm.tolerado.wsc.core.ToleradoSession;

public class WSMethodErrorHandlerFactory {
	private static final String CLS_ENTERPRISE_WS_METHOD_ERROR_HANDLER = "com.tgerm.tolerado.wsc.enterprise.EnterpriseWSMethodErrorHandler";
	private static final String CLS_PARTNER_WS_METHOD_ERROR_HANDLER = "com.tgerm.tolerado.wsc.partner.PartnerWSMethodErrorHandler";

	public static WSErrorHandler getErrorHandler(
			ToleradoSession.SessionType session) {
		String errorHandlerCls = null;
		switch (session) {
		case Enterprise:
			errorHandlerCls = CLS_ENTERPRISE_WS_METHOD_ERROR_HANDLER;
			break;
		default:
			errorHandlerCls = CLS_PARTNER_WS_METHOD_ERROR_HANDLER;
			break;
		}
		try {
			return (WSErrorHandler) Class.forName(errorHandlerCls)
					.newInstance();
		} catch (Exception e) {
			throw new ToleradoException(e);
		}
	}

}
