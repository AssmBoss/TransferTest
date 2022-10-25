package ru.netology.web.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import lombok.val;

import java.util.HashMap;
import java.util.Map;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;

public class DashboardPage {
    public SelenideElement heading = $x("//h1[contains(text(),'Ваши карты')]");
    private ElementsCollection cards = $$(".list__item div");
    private final String balanceStart = "баланс: ";
    private final String balanceFinish = " р.";
    private final String cardIdStart = "**** **** **** ";
    private final String cardIdFinish = ", баланс:";
    HashMap<String, Integer> balanceMap = new HashMap<>();
//    String cardId = "0001";

    public DashboardPage() {
        heading.shouldBe(visible);
        makeBalance();
//        clickButton(cardId);
    }

    public void makeBalance() {
        for (SelenideElement card : cards
        ) {
            balanceMap.put(extractCardId(card.text()), extractBalance(card.text()));
        }
//        System.out.println("баланс карты 0001 = " + getCardBalance("0001"));
//        System.out.println("баланс карты 0002 = " + getCardBalance("0002"));

//        for (Map.Entry<String, Integer> pair : balanceMap.entrySet()) {
//            String key = pair.getKey();
//            int value = pair.getValue();
//            System.out.println(key + ":" + value);
//        }
    }

    public int getCardBalance(String id) {
        // TODO: перебрать все карты и найти по атрибуту data-test-id
        //val text = cards.get(0).text();
        //val text = balanceMap.get(id).text();

        return balanceMap.get(id);
    }

    private int extractBalance(String text) {
        val start = text.indexOf(balanceStart);
        val finish = text.indexOf(balanceFinish);
        val value = text.substring(start + balanceStart.length(), finish);
        return Integer.parseInt(value);
    }

    private String extractCardId(String text) {
        val start = text.indexOf(cardIdStart);
        val finish = text.indexOf(cardIdFinish);
        val value = text.substring(start + cardIdStart.length(), finish);
        return value;
    }

    public TransferPage clickButton(String toCardId) {
        String xpathButton = "//div[contains(text(),'" + toCardId + "')]/button[@role='button']";
        $x(xpathButton).click();
        return new TransferPage();
    }
}
// ЗАГОТОВКА ДЛЯ КНОПКИ!!! $x("//div[contains(text(),'0001')]/button[@role='button']").click();