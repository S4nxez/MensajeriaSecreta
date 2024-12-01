package org.example.chatssecretos.domain.errors;

public sealed interface ErrorApp
        permits ErrorAppDataBase, ErrorAppUser, ErrorAppGroup,
        ErrorAppMessages, ErrorAppSecurity {}