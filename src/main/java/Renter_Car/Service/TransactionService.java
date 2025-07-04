package Renter_Car.Service;

import Renter_Car.Models.Transaction;
import Renter_Car.Models.User;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionService {
    List<Transaction> findTransactionByUser(User user);
    void saveTransaction(Transaction transaction);
    List<Transaction> findTransactionByDate(LocalDateTime from, LocalDateTime to, User user);
}
