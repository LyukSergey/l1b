package com.lss.v1_l1_app.rest;

import com.lss.repository.MoneyRepository;
import com.lss.v1_l1_app.service.TransferMoneyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController // ✅ Позначає клас як REST контролер
@RequiredArgsConstructor // ⚙️ Автоматично генерує конструктор для @Autowired полів
public class BankController {

    private final TransferMoneyService transferMoney; // 💡 Інжекція сервісу
    private final MoneyRepository moneyDao; // 💡 Інжекція репозиторію

    @GetMapping("/credit") // 🌐 Мапінг для GET запитів на /credit
    public ResponseEntity<String> credit(@RequestParam String name, @RequestParam Long amount, @RequestParam Long bankId) {
        // Звертаємося до сервісного шару для виконання бізнес-логіки
        final long resultedDeposit = transferMoney.transfer(name, amount, bankId); // 🔁 Взаємодія з сервісом
        if (resultedDeposit == 0) {
            final String response = String.format("Rejected %s won't survive this winter", name);// ❗ Повідомлення про відмову
            return ResponseEntity.ok(response);
        }
        final String response = String.format("Credit approved for %s. Current bank balance: %s", name, resultedDeposit);
        return ResponseEntity.ok(response); // ✅ Успішна відповідь
    }

    @GetMapping("/state") // 🌐 Мапінг для GET запитів на /state
    public double currentState() {
        // Звертаємося до репозиторію для отримання даних. Це не правильно
        return moneyDao.findAll().get(0).getTotalAmount(); // 🔁 Взаємодія з репозиторієм
    }

}

