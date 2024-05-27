package com.example.energotransbank.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.energotransbank.R;
import com.example.energotransbank.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Инициализация UI элементов
        TextView balanceTextView = binding.balanceTextView;
        RecyclerView recentTransactionsRecyclerView = binding.recentTransactionsRecyclerView;
        RecyclerView newsRecyclerView = binding.newsRecyclerView;

        // Установка данных баланса
        // Здесь вы можете получить баланс пользователя из базы данных или API
        balanceTextView.setText("Баланс: 10000 ₽");

        // Настройка списка последних транзакций
        // Здесь вы можете получить последние транзакции пользователя из базы данных или API
        recentTransactionsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recentTransactionsRecyclerView.setAdapter(new TransactionsAdapter());

        // Настройка списка новостей и уведомлений
        // Здесь вы можете получить новости и уведомления из базы данных или API
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        newsRecyclerView.setAdapter(new NewsAdapter());

        // Настройка кнопок быстрых действий
        binding.transferButton.setOnClickListener(v -> {
            // Логика для кнопки перевода
        });

        binding.payBillsButton.setOnClickListener(v -> {
            // Логика для кнопки оплаты счетов
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_transaction, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            // Установка данных для каждой транзакции
            holder.transactionTextView.setText("Транзакция " + position);
        }

        @Override
        public int getItemCount() {
            return 5; // Количество транзакций для отображения
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView transactionTextView;

            ViewHolder(View itemView) {
                super(itemView);
                transactionTextView = itemView.findViewById(R.id.transactionTextView);
            }
        }
    }

    private class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_news, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            // Установка данных для каждой новости или уведомления
            holder.newsTextView.setText("Новость " + position);
        }

        @Override
        public int getItemCount() {
            return 5; // Количество новостей для отображения
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView newsTextView;

            ViewHolder(View itemView) {
                super(itemView);
                newsTextView = itemView.findViewById(R.id.newsTextView);
            }
        }
    }
}
