package com.devcrutch;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import java.util.Hashtable;

public class LdapAuthentication {


    private String ldapURI;
    private final static String CONTEXT_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";

    public LdapAuthentication(String ldapURI) {
        this.ldapURI = ldapURI;
    }

    private DirContext getContext() throws Exception {
        Hashtable<String, String> env = new Hashtable<String, String>();
        return getContext(env);
    }

    private DirContext getContext(Hashtable<String, String> env) throws Exception {
        env.put(Context.INITIAL_CONTEXT_FACTORY, CONTEXT_FACTORY);
        env.put(Context.PROVIDER_URL, ldapURI);
        DirContext ctx = new InitialDirContext(env);
        return ctx;
    }

    private String findDN(String user) throws Exception {
        DirContext ctx = getContext();
        String dn = null;
        String filter = "(&(objectClass=*)(uid=" + user + "))";
        NamingEnumeration answer = ctx.search("", filter, getSimpleSearchControls());
        if (answer.hasMore()) {
            SearchResult result = (SearchResult) answer.next();
            dn = result.getNameInNamespace();
        }
        answer.close();
        return dn;
    }

    private SearchControls getSimpleSearchControls() {
        SearchControls controls = new SearchControls();
        controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        controls.setTimeLimit(3000);
        return controls;
    }

    public DirContext bind(String username, String password) throws Exception {
        DirContext dirContext;
        Hashtable<String, String> env = new Hashtable<String, String>();
        String dn = findDN(username);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, dn);
        env.put(Context.SECURITY_CREDENTIALS, password);
        try {
            dirContext = getContext(env);
        } catch (javax.naming.AuthenticationException e) {
            throw new Exception(e);
        }
        return dirContext;
    }
}
