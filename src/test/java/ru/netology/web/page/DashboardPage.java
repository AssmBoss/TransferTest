package ru.netology.web.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import lombok.val;
import org.junit.jupiter.api.Assertions;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class DashboardPage {
    private SelenideElement heading = $x("//h1[contains(text(),'Ваши карты')]");
    private ElementsCollection cards = $$(".list__item div");
    private final String balanceStart = "баланс: ";
    private final String balanceFinish = " р.";
    private final String cardIdStart = "**** **** **** ";
    private final String cardIdFinish = ", баланс:";

    public DashboardPage() {
        heading.shouldBe(visible);
    }

    public void visible() {
        heading.shouldBe(visible);
    }

    public int getCardBalance(String id) {
        visible();
        String value = "";
        for (SelenideElement card : cards) {
            if (id.equals(extractCardInfo(card.text(), cardIdStart, cardIdFinish))) {
                value = extractCardInfo(card.text(), balanceStart, balanceFinish);
            }
        }
        if (value == "") {
            Assertions.fail("getCardBalance -> Тест упал: не найден баланс карты " + id);
        }
        return Integer.parseInt(value);
    }

    private String extractCardInfo(String text, String textStart, String textFinish) {
        val start = text.indexOf(textStart);
        val finish = text.indexOf(textFinish);
        return text.substring(start + textStart.length(), finish);
    }

    public TransferPage clickButton(String toCardId) {
        String xpathButton = "//div[contains(text(),'" + toCardId + "')]/button[@role='button']";
        $x(xpathButton).click();
        return new TransferPage();
    }
}
