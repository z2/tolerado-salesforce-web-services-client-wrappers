package com.tgerm.tolerado.wsc.samples;

import com.sforce.soap.apex.RunTestsRequest;
import com.sforce.soap.apex.RunTestsResult;
import com.sforce.soap.apex.SoapConnection;
import com.sforce.soap.enterprise.EnterpriseConnection;
import com.sforce.soap.enterprise.QueryResult;
import com.sforce.soap.enterprise.sobject.Contact;
import com.sforce.soap.metadata.DescribeMetadataResult;
import com.sforce.soap.metadata.MetadataConnection;
import com.sforce.soap.partner.Connector;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.SaveResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;
import com.tgerm.tolerado.samples.cfg.Credential;
import com.tgerm.tolerado.samples.cfg.LoginCfg;

/**
 * @author abhinav
 */
public class WSCSessionSample {

	public static void main(String[] args) throws ConnectionException {
		Credential credential = LoginCfg.self.getCredential();
		WSCSession session = new WSCSession(WSCSession.LoginWSDL.EnterpriseWSDL,
				credential.getUserName(), credential.getPassword());

		// Partner WSDL Code Sample
		doPartner(session);

		// Metadata WSDL Code Sample
		doMeta(session);

		// Apex WSDL Code Sample
		doApex(session);

		// Enterprise WSDL Code Sample
		doEnterprise(session);

	}

	private static void doEnterprise(WSCSession sessionFactory)
			throws ConnectionException {
		// 
		// Create Enterprise Connection
		//
		ConnectorConfig entCfg = new ConnectorConfig();
		entCfg.setManualLogin(true);
		entCfg.setServiceEndpoint(sessionFactory.getEnterpriseServerUrl());
		EnterpriseConnection entConn = com.sforce.soap.enterprise.Connector
				.newConnection(entCfg);
		entConn.setSessionHeader(sessionFactory.getSessionId());

		// Enterprise WSDL for query
		QueryResult queryResults = entConn
				.query("SELECT Id, FirstName, LastName FROM Contact LIMIT 1");
		if (queryResults.getSize() > 0) {
			Contact c = (Contact) queryResults.getRecords()[0];
			System.out.println("EnterPrise WSDL : Queried Contact Name: "
					+ c.getFirstName() + " " + c.getLastName());
		}
	}

	private static void doPartner(WSCSession sessionFactory)
			throws ConnectionException {
		//
		// Create new Partner Connection
		//
		ConnectorConfig config = new ConnectorConfig();
		config.setManualLogin(true);
		config.setServiceEndpoint(sessionFactory.getPartnerServerUrl());
		config.setSessionId(sessionFactory.getSessionId());
		PartnerConnection partnerConn = Connector.newConnection(config);
		partnerConn.setSessionHeader(sessionFactory.getSessionId());

		// Create a new Contact
		SObject contact = new SObject();
		contact.setType("Contact");
		contact.setField("FirstName", "Abhinav");
		contact.setField("LastName", "Gupta");
		SaveResult[] create = partnerConn.create(new SObject[] { contact });
		for (SaveResult saveResult : create) {
			if (!saveResult.isSuccess()) {
				throw new RuntimeException(
						"Failed to save contact via Partner WSDL");
			}
		}
		System.out.println("Partner-WSDL : Save successfully done");
	}

	private static void doApex(WSCSession session) throws ConnectionException {
		//
		// Create Apex Connection
		//
		ConnectorConfig apexConfig = new ConnectorConfig();
		apexConfig.setSessionId(session.getSessionId());
		String apexEndpoint = session.getApexServerUrl();
		apexConfig.setServiceEndpoint(apexEndpoint);
		SoapConnection apexConnection = com.sforce.soap.apex.Connector
				.newConnection(apexConfig);

		// Run All Tests in the org
		RunTestsRequest runTestsRequest = new RunTestsRequest();
		runTestsRequest.setAllTests(true);
		runTestsRequest.setNamespace("");
		RunTestsResult runTests = apexConnection.runTests(runTestsRequest);
		System.out.println(" Apex-WSDL: NumTestsRun :  "
				+ runTests.getNumTestsRun());
	}

	private static void doMeta(WSCSession session) throws ConnectionException {
		// 
		// Create Metadata Connection
		//
		ConnectorConfig metadataConfig = new ConnectorConfig();
		metadataConfig.setSessionId(session.getSessionId());
		// Set the metdata server url from LoginResult
		metadataConfig.setServiceEndpoint(session.getMetadataServerUrl());
		MetadataConnection metadataConnection = com.sforce.soap.metadata.Connector
				.newConnection(metadataConfig);

		// Try describing the metadata
		DescribeMetadataResult describeMetadata = metadataConnection
				.describeMetadata(15.0);
		System.out
				.println("Metadata WSDL : OrgName from describeMetadata() call"
						+ describeMetadata.getOrganizationNamespace());
	}
}
