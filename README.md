# Selenide + Kotlin/JS

## What is this repository

This is playground for PoC of Kotlin/JS Selenide tests

## What is Selenide?

Selenide is a framework for writing easy-to-read and easy-to-maintain automated tests in Java.
It defines concise fluent API, natural language assertions and does some magic for ajax-based applications to let you focus entirely on the business logic of your tests.

Selenide is based on and is compatible to Selenium WebDriver 2.0+ and 3.0+

    @Test
    public void testLogin() {
      open("/login");
      $(By.name("user.name")).setValue("johny");
      $("#submit").click();
      $("#username").shouldHave(text("Hello, Johny!"));
    }

Look for [detailed comparison of Selenide and Selenium WebDriver API](https://github.com/selenide/selenide/wiki/Selenide-vs-Selenium).
