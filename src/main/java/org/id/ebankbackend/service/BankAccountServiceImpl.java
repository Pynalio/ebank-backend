package org.id.ebankbackend.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.id.ebankbackend.entities.*;
import org.id.ebankbackend.enums.OperationType;
import org.id.ebankbackend.exceptions.BankAccountNotFoundException;
import org.id.ebankbackend.exceptions.BlanceNotSufficentException;
import org.id.ebankbackend.exceptions.CustomerNotFoundException;
import org.id.ebankbackend.repositories.AccountOpetrationRepository;
import org.id.ebankbackend.repositories.BankAccountRepository;
import org.id.ebankbackend.repositories.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;


@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class BankAccountServiceImpl implements BankAccountService{

    private CustomerRepository customerRepository;

    private BankAccountRepository bankAccountRepository;

    private AccountOpetrationRepository accountOpetrationRepository;
    /*Slf4j*/
    /*Logger log= LoggerFactory.getLogger(this.getClass().getName());*/

    @Override
    public Customer saveCustomer(Customer customer) {
        log.info("Saving new Customer");
        Customer savedCustomer= customerRepository.save(customer);
        return savedCustomer ;
    }

    @Override
    public CurrentACCOUNT saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException {

        Customer customer =customerRepository.findById(customerId).orElse(null);
        if (customer==null)
            throw new CustomerNotFoundException("Customer not found");
        CurrentACCOUNT currentACCOUNT=new CurrentACCOUNT();
        currentACCOUNT.setId(UUID.randomUUID().toString());
        currentACCOUNT.setCreatAt(new Date());
        currentACCOUNT.setBalance(initialBalance);
        currentACCOUNT.setOverDraft(overDraft);
        currentACCOUNT.setCustomer(customer);

        CurrentACCOUNT savedBankAccount= bankAccountRepository.save(currentACCOUNT);

        return savedBankAccount;


    }

    @Override
    public SavingAccount  saveSavingBankAccount(double initialBalance, double interestRate, Long customerId) throws CustomerNotFoundException {
        Customer customer =customerRepository.findById(customerId).orElse(null);
        if (customer==null)
            throw new CustomerNotFoundException("Customer not found");
        SavingAccount savingAccount=new SavingAccount( );
        savingAccount.setId(UUID.randomUUID().toString());
        savingAccount.setCreatAt(new Date());
        savingAccount.setBalance(initialBalance);
        savingAccount.setInterestRate(interestRate);
        savingAccount.setCustomer(customer);

        SavingAccount savedBankAccount= bankAccountRepository.save(savingAccount);

        return savedBankAccount;

    }




    @Override
    public List<Customer> listCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public BankAccount getBankAccount(String accountId) throws BankAccountNotFoundException {

        BankAccount bankAccount= bankAccountRepository.findById(accountId).orElseThrow(()->new BankAccountNotFoundException("Bank noy found"));

        return null;
    }

    @Override
    public void debit(String accountId, double amount, String description) throws BankAccountNotFoundException, BlanceNotSufficentException {
        BankAccount bankAccount=bankAccountRepository.findById(accountId)
                .orElseThrow(()->new BankAccountNotFoundException("BankAccount not found"));
        if(bankAccount.getBalance()<amount){
            throw new BlanceNotSufficentException("Balance not sufficient");
        }
        AccountOperation x=new AccountOperation();
        x.setType(OperationType.DEBIT);
        x.setAmount(amount);
        x.setDescription(description);
        x.setOperationDate(new Date());
        x.setBankAccount(bankAccount);

        accountOpetrationRepository.save(x);
        bankAccount.setBalance(bankAccount.getBalance()-amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void credit(String accountId, double amount, String description) throws BankAccountNotFoundException{
        BankAccount bankAccount=bankAccountRepository.findById(accountId)
                .orElseThrow(()->new BankAccountNotFoundException("BankAccount not found"));

        AccountOperation x=new AccountOperation();
        x.setType(OperationType.CREDIT);
        x.setAmount(amount);
        x.setDescription(description);
        x.setOperationDate(new Date());
        x.setBankAccount(bankAccount);

        accountOpetrationRepository.save(x);
        bankAccount.setBalance(bankAccount.getBalance()+amount);
        bankAccountRepository.save(bankAccount);

    }

    @Override
    public void transfer(String accountIdSource, String accountIdDestination, double amount) throws BlanceNotSufficentException, BankAccountNotFoundException {
        debit(accountIdSource,amount,"Transfer to "+ accountIdDestination);
        credit(accountIdDestination,amount,"Transfer from "+ accountIdSource );
    }


    @Override
    public List<BankAccount> bankAccountList(){
        return bankAccountRepository.findAll();
    }
}
