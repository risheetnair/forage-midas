package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Component
public class DatabaseConduit {
    private final UserRepository userRepository;
    private final TransactionRecordRepository transactionRecordRepository;

    public DatabaseConduit(UserRepository userRepository,
            TransactionRecordRepository transactionRecordRepository) {
        this.userRepository = userRepository;
        this.transactionRecordRepository = transactionRecordRepository;
    }

    @Transactional
    public void process(Transaction tx) {
        Optional<UserRecord> senderOpt = userRepository.findById(tx.getSenderId());
        if (senderOpt.isEmpty()) {
            return;
        }

        Optional<UserRecord> recipientOpt = userRepository.findById(tx.getRecipientId());
        if (recipientOpt.isEmpty()) {
            return;
        }

        UserRecord sender = senderOpt.get();
        UserRecord recipient = recipientOpt.get();

        float amount = tx.getAmount();

        if (sender.getBalance() < amount) {
            return;
        }

        sender.setBalance(sender.getBalance() - amount);
        recipient.setBalance(recipient.getBalance() + amount);

        userRepository.save(sender);
        userRepository.save(recipient);

        TransactionRecord record = new TransactionRecord(sender, recipient, amount);
        transactionRecordRepository.save(record);
    }

    public void save(UserRecord userRecord) {
        userRepository.save(userRecord);
    }

    public float getBalanceByName(String name) {
        UserRecord user = userRepository.findByName(name);
        return user.getBalance();
    }

}
