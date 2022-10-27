package ru.netology.web.test;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.web.data.DataHelper;
import ru.netology.web.page.DashboardPage;
import ru.netology.web.page.LoginPageV3;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.open;

import java.time.Duration;

class MoneyTransferTest {

    private DashboardPage dashboardPage;

    @BeforeEach
    public void setupAndCheckBalance() {
        Configuration.holdBrowserOpen = false;
        var loginPage = open("http://localhost:9999", LoginPageV3.class);
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        dashboardPage = verificationPage.validVerify(verificationCode);
        Assertions.assertTrue(dashboardPage.getCardBalance("0001") + dashboardPage.getCardBalance("0002") == 20000, "@BeforeEach: Сумма балансов всех карт должна быть 20 000 руб.! \n Нарушена целостность тестовой среды!");
    }

    @AfterEach
    public void returnBalance() throws InterruptedException {
        Configuration.holdBrowserOpen = false;
        var loginPage = open("http://localhost:9999", LoginPageV3.class);
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        dashboardPage = verificationPage.validVerify(verificationCode);
        int delta = dashboardPage.getCardBalance("0001") - 10_000;
        if (delta > 0) {
            var transferPage = dashboardPage.clickButton("0002");
            transferPage.transferMoney(delta, "0001");
        } else {
            var transferPage = dashboardPage.clickButton("0001");
            transferPage.transferMoney(-delta, "0002");
        }
        Assertions.assertTrue((dashboardPage.getCardBalance("0001") == 10_000) && (dashboardPage.getCardBalance("0002") == 10_000), "@AfterEach: Не удалось вернуть балансы карт к исходным 10 000 руб.!");
    }

    @Test
    void correctTransferFromCard1toCard2() {
        Assertions.assertTrue(dashboardPage.getCardBalance("0001") > 0, "Баланс карты должен быть больше нуля! \nПополнение невозможно! \n ");
        int randomCash = DataHelper.randomCash(dashboardPage.getCardBalance("0001"));
        int expectedReceiver = dashboardPage.getCardBalance("0002") + randomCash;
        int expectedSource = dashboardPage.getCardBalance("0001") - randomCash;
        var transferPage = dashboardPage.clickButton("0002");
        transferPage.transferMoney(randomCash, "0001");
        Assertions.assertEquals(expectedReceiver, dashboardPage.getCardBalance("0002"), "Перевод неверный: баланс карты-приемника увеличился НЕ на сумму перевода!");
        Assertions.assertEquals(expectedSource, dashboardPage.getCardBalance("0001"), "Перевод неверный: баланс карты-источника уменьшился НЕ на сумму перевода!");
    }

    @Test
    void correctTransferFromCard2toCard1() {
        Assertions.assertTrue(dashboardPage.getCardBalance("0002") > 0, "Баланс карты должен быть больше нуля! \nПополнение невозможно! \n ");
        int randomCash = DataHelper.randomCash(dashboardPage.getCardBalance("0002"));
        int expectedReceiver = dashboardPage.getCardBalance("0001") + randomCash;
        int expectedSource = dashboardPage.getCardBalance("0002") - randomCash;
        var transferPage = dashboardPage.clickButton("0001");
        transferPage.transferMoney(randomCash, "0002");
        Assertions.assertEquals(expectedReceiver, dashboardPage.getCardBalance("0001"), "Перевод неверный: баланс карты-приемника увеличился НЕ на сумму перевода!");
        Assertions.assertEquals(expectedSource, dashboardPage.getCardBalance("0002"), "Перевод неверный: баланс карты-источника уменьшился НЕ на сумму перевода!");
    }

    @Test
    void excessiveTransferFromCard1toCard2() {
        Assertions.assertTrue(dashboardPage.getCardBalance("0001") > 0, "Баланс карты должен быть больше нуля! \nПополнение невозможно! \n ");
        int excessiveCash = dashboardPage.getCardBalance("0001") + 1000;
        var transferPage = dashboardPage.clickButton("0002");
        transferPage.transferMoney(excessiveCash, "0001");
        transferPage.error.should(appear, Duration.ofSeconds(10));
    }

    @Test
    void excessiveTransferFromCard2toCard1() {
        Assertions.assertTrue(dashboardPage.getCardBalance("0002") > 0, "Баланс карты должен быть больше нуля! \nПополнение невозможно! \n ");
        int excessiveCash = dashboardPage.getCardBalance("0002") + 1000;
        var transferPage = dashboardPage.clickButton("0001");
        transferPage.transferMoney(excessiveCash, "0002");
        transferPage.error.should(appear, Duration.ofSeconds(10));
    }

    @Test
    void transferToCard1fromIncorrectCard() {
        Assertions.assertTrue(dashboardPage.getCardBalance("0002") > 0, "Баланс карты должен быть больше нуля! \nПополнение невозможно! \n ");
        int cash = dashboardPage.getCardBalance("0002");
        var transferPage = dashboardPage.clickButton("0001");
        String incorrectCard = "9999";
        transferPage.transferMoney(cash, incorrectCard);
        transferPage.error.should(appear, Duration.ofSeconds(10));
    }

    @Test
    void transferToCard2fromIncorrectCard() {
        Assertions.assertTrue(dashboardPage.getCardBalance("0001") > 0, "Баланс карты должен быть больше нуля! \nПополнение невозможно! \n ");
        int cash = dashboardPage.getCardBalance("0001");
        var transferPage = dashboardPage.clickButton("0002");
        String incorrectCard = "9999";
        transferPage.transferMoney(cash, incorrectCard);
        transferPage.error.should(appear, Duration.ofSeconds(10));
    }
}

