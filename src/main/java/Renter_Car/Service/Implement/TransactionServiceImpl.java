package Renter_Car.Service.Implement;

import Renter_Car.Models.Transaction;
import Renter_Car.Models.User;
import Renter_Car.Repository.TransactionRepository;
import Renter_Car.Service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public List<Transaction> findTransactionByUser(User user) {
        return transactionRepository.findTransactionsByUser(user, Sort.by(Sort.Order.desc("transactionDate")));
    }

    @Override
    public void saveTransaction(Transaction transaction) {
        transactionRepository.save(transaction);
    }

    @Override
    public List<Transaction> findTransactionByDate(LocalDateTime from, LocalDateTime to, User user) {
        return transactionRepository.findTransactionsByUserAndDateRange(user, from, to);
    }
}
