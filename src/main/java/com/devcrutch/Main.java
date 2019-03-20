package com.devcrutch;

import javax.naming.directory.DirContext;

public class Main {
    public static void main(String[] args) {
        LdapAuthentication ldapAuthentication = new LdapAuthentication("ldap://192.168.1.5/dc=lab,dc=devcrutch,dc=com");
        try {
            DirContext dirContext = ldapAuthentication.bind("vhashemi","1234");
            if (dirContext != null)
                System.out.println("Authorized");
        } catch (Exception e) {
            System.out.println("Unauthorized");

        }

    }
}
