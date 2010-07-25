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

package com.tgerm.tolerado.axis14.enterprise;

import org.apache.axis.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sforce.soap.enterprise.fault.ApiFault;
import com.sforce.soap.enterprise.fault.ExceptionCode;
import com.tgerm.tolerado.axis14.core.method.WSMethodErrorHandler;
import com.tgerm.tolerado.axis14.partner.PartnerUtil;

public class EnterpriseWSMethodErrorHandler implements WSMethodErrorHandler {
	private static final Log log = LogFactory
			.getLog(EnterpriseWSMethodErrorHandler.class);

	@Override
	public boolean canRetry(Exception t) {
		if (t instanceof ApiFault) {
			ApiFault af = (ApiFault) t;
			ExceptionCode code = af.getExceptionCode();
			log.debug("code:" + code);
			if (code != null)
				return RETRYABLES.contains(code.getValue().toLowerCase());
		} else if (t instanceof AxisFault) {
			AxisFault af = (AxisFault) t;
			if (af.getCause() != null
					&& af.getCause() instanceof java.net.SocketException) {
				return true;
			}

			String faultString = af.getFaultString();
			if (faultString != null
					&& faultString.indexOf("UnknownHostException") != -1) {
				return true;
			}

			String exCode = PartnerUtil.faultCodeFromAxisFault(af);
			log.debug("code:" + exCode);
			if (exCode != null) {
				return RETRYABLES.contains(exCode.toLowerCase());
			}
		}
		return false;
	}

	@Override
	public boolean isLoginExpired(Exception t) {
		if (t instanceof ApiFault) {
			ExceptionCode code = null;
			ApiFault af = (ApiFault) t;
			code = af.getExceptionCode();
			return code != null
					&& ExceptionCode.INVALID_SESSION_ID.equals(code);
		} else if (t instanceof AxisFault) {
			AxisFault af = (AxisFault) t;
			String exCode = PartnerUtil.faultCodeFromAxisFault(af);
			if (exCode != null) {
				return exCode.equalsIgnoreCase("INVALID_SESSION_ID");
			}
		}
		return false;
	}

}
