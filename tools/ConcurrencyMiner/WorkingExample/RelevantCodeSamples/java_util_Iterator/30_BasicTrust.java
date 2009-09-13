/*
 * Copyright [2005] [University Corporation for Advanced Internet Development, Inc.]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.internet2.middleware.shibboleth.common.provider;

import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.keys.keyresolver.KeyResolverException;
import org.opensaml.SAMLException;
import org.opensaml.SAMLSignedObject;

import edu.internet2.middleware.shibboleth.common.Trust;
import edu.internet2.middleware.shibboleth.metadata.KeyDescriptor;
import edu.internet2.middleware.shibboleth.metadata.RoleDescriptor;

/**
 * <code>Trust</code> implementation that validates against standard inline keying data within SAML 2 metadata.
 *
 * @author Walter Hoehn
 */
public class BasicTrust implements Trust {

	private static Logger log = Logger.getLogger(BasicTrust.class.getName());

	/*
	 * @see edu.internet2.middleware.shibboleth.common.Trust#validate(java.security.cert.X509Certificate,
	 *      java.security.cert.X509Certificate[], edu.internet2.middleware.shibboleth.metadata.RoleDescriptor, boolean)
	 */
	public boolean validate(X509Certificate certificateEE, X509Certificate[] certificateChain,
			RoleDescriptor descriptor, boolean checkName) {

		if (descriptor == null || certificateEE == null) {
			log.error("Appropriate data was not supplied for trust evaluation.");
			return false;
		}

		// Iterator through all the keys in the metadata
		Iterator keyDescriptors = descriptor.getKeyDescriptors();
		while (keyDescriptors.hasNext()) {
			// Look for a key descriptor with the right usage bits
			KeyDescriptor keyDescriptor = (KeyDescriptor) keyDescriptors.next();
			if (keyDescriptor.getUse() == KeyDescriptor.ENCRYPTION) {
				log.debug("Skipping key descriptor with inappropriate usage indicator.");
				continue;
			}

			// We found one, attempt to do an exact match between the metadata certificate
			// and the supplied end-entity certificate
			KeyInfo keyInfo = keyDescriptor.getKeyInfo();
			log.debug("Attempting to match X509 certificate.");
			try {
				PublicKey eeKey = certificateEE.getPublicKey();
				for(int i = 0; i < keyInfo.lengthKeyValue(); i++){
					if(eeKey.equals(keyInfo.itemKeyValue(i))){
						log.debug("Matched entity certificate key against key info key value");
					}
				}

				X509Certificate metaCert = keyInfo.getX509Certificate();
				if(metaCert != null){
					if (eeKey.equals(metaCert.getPublicKey())) {
						log.debug("Matched entity certificate key against key info's certificate's public key");
						return true;
					}
				}
			} catch (KeyResolverException e) {
				log.error("Error extracting X509 certificate from metadata.", e);
			} catch (XMLSecurityException e) {
				log.error("Unable to read public keys from metadata", e);
			}
		}
		return false;
	}

	/*
	 * @see edu.internet2.middleware.shibboleth.common.Trust#validate(java.security.cert.X509Certificate,
	 *      java.security.cert.X509Certificate[], edu.internet2.middleware.shibboleth.metadata.RoleDescriptor)
	 */
	public boolean validate(X509Certificate certificateEE, X509Certificate[] certificateChain, RoleDescriptor descriptor) {

		return validate(certificateEE, certificateChain, descriptor, true);
	}

	/*
	 * @see edu.internet2.middleware.shibboleth.common.Trust#validate(org.opensaml.SAMLSignedObject,
	 *      edu.internet2.middleware.shibboleth.metadata.RoleDescriptor)
	 */
	public boolean validate(SAMLSignedObject token, RoleDescriptor descriptor) {

		/*
		 * Run through the Role Metadata testing Public Keys
		 */
		Iterator ikeyDescriptors = descriptor.getKeyDescriptors();
		while (ikeyDescriptors.hasNext()) {
			KeyDescriptor keyDescriptor = (KeyDescriptor) ikeyDescriptors.next();
			if (keyDescriptor.getUse() != KeyDescriptor.ENCRYPTION) {
				// KeyInfo can be used for signing
				KeyInfo keyInfo = keyDescriptor.getKeyInfo();
				try {
					// XMLSEC drills down to extract a Public Key
					PublicKey publicKey = keyInfo.getPublicKey();
					try {
						token.verify(publicKey);
						return true;
					} catch (SAMLException e) {
						continue;
					}
				} catch (KeyResolverException e) {
					continue;
				}
			}
		}
		return false;
	}
}
