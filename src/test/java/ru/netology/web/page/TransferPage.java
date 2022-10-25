package ru.netology.web.page;

import com.codeborne.selenide.SelenideElement;
import ru.netology.web.data.DataHelper;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class TransferPage {
    public SelenideElement error = $("[data-test-id=error-notification]");
    private SelenideElement heading = $x("//h1[contains(text(),'Пополнение карты')]");
    private SelenideElement amountField = $("[data-test-id=amount] input");
    private SelenideElement cardField = $("[data-test-id=from] input");
    private SelenideElement transferButton = $("[data-test-id=action-transfer]");
    private final String hiddenNumberCardPart = "5559 0000 0000 ";

    public TransferPage() {
        heading.shouldBe(visible);
    }

    public void transferMoney(int amount, String fromCardId) {
        amountField.setValue(String.valueOf(amount));
        cardField.setValue(hiddenNumberCardPart + fromCardId);
        transferButton.click();
    }
}
