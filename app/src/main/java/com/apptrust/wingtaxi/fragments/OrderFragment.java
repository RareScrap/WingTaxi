package com.apptrust.wingtaxi.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.apptrust.wingtaxi.MainActivity;
import com.apptrust.wingtaxi.R;
import com.apptrust.wingtaxi.utils.Adres;

import java.util.ArrayList;

/**
 * Фрагмент настойки адресов поездки. Позволяет пользователю задать дополительые адреса
 * и назачит время к которому должно быть подано такси
 * @author RareScrap
 */
public class OrderFragment extends Fragment {
    /** Список адресов, которые выбрал пользователь */
    public ArrayList<Adres> adreses = new ArrayList<>();
    /** UI списка */
    private RecyclerView mRecyclerView;
    /** Адаптер списка */
    private TaxiListAdapter mTaxiListAdapter;
    /** Кнопка добавления дополнительного адреса */
    private ImageButton addAdresImageButton;
    /** Кнопка выбора времени подачи такси */
    private ImageButton timeSetImageButton;
    /** {@link TextView} над кнопкой "выбрать время" */
    private TextView timeTextView;
    /** Кнопка перехода к следующему фрагменту */
    private Button nextButton;

    /**
     * Используйте этот фабричный метод для создания новых экземпляров
     * этого фрагмента с использованием предоставленных параметров
     * @param firstAdres Первый адрес, с которого пользователь начинает путь
     * @return Новый объект фрагмента {@link OrderFragment}
     */
    public static OrderFragment newInstance(Adres firstAdres) {
        OrderFragment fragment = new OrderFragment();

        // Добавляет первый адрес в список адресов
        fragment.adreses.add(firstAdres);

        // Вернуть новый экземпляр фрагмента
        return fragment;
    }


    /**
     * Called to have the fragment instantiate its user interface view.
     * This is optional, and non-graphical fragments can return null (which
     * is the default implementation).  This will be called between
     * {@link #onCreate(Bundle)} and {@link #onActivityCreated(Bundle)}.
     * <p>
     * <p>If you return a View from here, you will later be called in
     * {@link #onDestroyView} when the view is being released.
     *
     * Инициализирует {@link #mRecyclerView} и вызывает UI фрагмента.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View returnedView = inflater.inflate(R.layout.fragment_order, container, false);

        // Инициализация UI списка
        mRecyclerView = (RecyclerView) returnedView.findViewById(R.id.adresRecyclerView);
        addAdresImageButton = (ImageButton) returnedView.findViewById(R.id.addAdresImageButton);
        timeSetImageButton = (ImageButton) returnedView.findViewById(R.id.timeSetImageButton);
        nextButton = (Button) returnedView.findViewById(R.id.next_button);
        timeTextView = (TextView) returnedView.findViewById(R.id.selectedTimeTextView);
        mTaxiListAdapter = new TaxiListAdapter(adreses, deleteClickListener);
        mRecyclerView.setAdapter(mTaxiListAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Названичение текста actionBar'у
        ActionBar ab = ((MainActivity) this.getActivity()).getSupportActionBar();
        ab.setTitle(R.string.trip_settings); // Вывести в титульую строку название блюда
        ab.setSubtitle(""); // Стереть подстроку

        // Устаовка слушателя на кнопки
        addAdresImageButton.setOnClickListener(addAdresClickListener);
        timeSetImageButton.setOnClickListener(timeSetClickListener);
        nextButton.setOnClickListener(nextClickListener);


        // Вернуть UI фрагмента
        return returnedView;
    }

    public class TaxiListAdapter extends RecyclerView.Adapter<TaxiListAdapter.ViewHolder> {
        /**
         * Список адресов, которые выбрал пользователь
         */
        public ArrayList<Adres> adreses = new ArrayList<>();
        private final View.OnClickListener clickListener;

        /**
         * Конструктор, инициализирующий свои поля
         *
         * @param adreses       Список адресов, которые нужно отобразить в элеметах списка
         * @param clickListener слушатель клика по кнопке "удалить"
         */
        public TaxiListAdapter(ArrayList<Adres> adreses, View.OnClickListener clickListener) {
            this.adreses = adreses;
            this.clickListener = clickListener;
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            public final TextView adresTextView;
            public final ImageButton deleteButton;

            public ViewHolder(View itemView, View.OnClickListener clickListener) {
                super(itemView);

                // Получение ссылок на элеметы UI
                adresTextView = (TextView) itemView.findViewById(R.id.adresTextView);
                deleteButton = (ImageButton) itemView.findViewById(R.id.deleteButton);

                // Связывание слушателя со кнопкой "удалить"
                deleteButton.setOnClickListener(clickListener);
            }
        }

        /**
         * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
         * an item.
         * <p>
         * This new ViewHolder should be constructed with a new View that can represent the items
         * of the given type. You can either create a new View manually or inflate it from an XML
         * layout file.
         * <p>
         * The new ViewHolder will be used to display items of the adapter using
         * {@link #onBindViewHolder(ViewHolder, int, List)}. Since it will be re-used to display
         * different items in the data set, it is a good idea to cache references to sub views of
         * the View to avoid unnecessary {@link View#findViewById(int)} calls.
         *
         * @param parent   The ViewGroup into which the new View will be added after it is bound to
         *                 an adapter position.
         * @param viewType The view type of the new View.
         * @return A new ViewHolder that holds a View of the given view type.
         * @see #getItemViewType(int)
         * @see #onBindViewHolder(ViewHolder, int)
         */
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // Заполнение макета item'а списка
            View view = LayoutInflater.from( parent.getContext() ).inflate(R.layout.item_fragment_order_recyclerview, parent, false);

            // Создание ViewHolder для текущего элемента
            return (new ViewHolder(view, clickListener));
        }

        /**
         * Called by RecyclerView to display the data at the specified position. This method should
         * update the contents of the {@link ViewHolder#itemView} to reflect the item at the given
         * position.
         * <p>
         * Note that unlike {@link ListView}, RecyclerView will not call this method
         * again if the position of the item changes in the data set unless the item itself is
         * invalidated or the new position cannot be determined. For this reason, you should only
         * use the <code>position</code> parameter while acquiring the related data item inside
         * this method and should not keep a copy of it. If you need the position of an item later
         * on (e.g. in a click listener), use {@link ViewHolder#getAdapterPosition()} which will
         * have the updated adapter position.
         * <p>
         * Override {@link #onBindViewHolder(ViewHolder, int, List)} instead if Adapter can
         * handle efficient partial bind.
         *
         * @param holder   The ViewHolder which should be updated to represent the contents of the
         *                 item at the given position in the data set.
         * @param position The position of the item within the adapter's data set.
         */
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // Получение объекта FoodItem для заданной позиции ListView
            Adres adresItem = adreses.get(position);

            // Обработка поведения View'хи при разных количествах элементов в списке
            View topLine = holder.itemView.findViewById(R.id.top_line);
            View bottomLine = holder.itemView.findViewById(R.id.bottom_line);
            topLine.setVisibility(View.VISIBLE);
            bottomLine.setVisibility(View.VISIBLE);
            if (adreses.size() == position+1 && adreses.size() == 1) {
                topLine.setVisibility(View.INVISIBLE);
                bottomLine.setVisibility(View.INVISIBLE);
            } else {
                if (position == 0)
                    topLine.setVisibility(View.INVISIBLE);
                if (position == adreses.size()-1)
                    bottomLine.setVisibility(View.INVISIBLE);
            }


            // Назначения текста элементам GUI
            holder.adresTextView.setText(adresItem.textAdres);
        }

        /**
         * Returns the total number of items in the data set held by the adapter.
         *
         * @return The total number of items in this adapter.
         */
        @Override
        public int getItemCount() {
            return adreses.size();
        }
    }

    View.OnClickListener deleteClickListener = new View.OnClickListener() {
        /**
         * Called when a view has been clicked.
         *
         * @param view The view that was clicked.
         */
        @Override
        public void onClick(View view) {
            // TODO: Убрать цепучку getParent
            TaxiListAdapter.ViewHolder viewHolder = (TaxiListAdapter.ViewHolder) mRecyclerView.getChildViewHolder((View) view.getParent().getParent().getParent());
            int pos = viewHolder.getLayoutPosition();
            mTaxiListAdapter.adreses.remove(pos);
            mTaxiListAdapter.notifyDataSetChanged();
        }
    };

    View.OnClickListener addAdresClickListener = new View.OnClickListener() {
        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            FragmentTransaction fTrans = getFragmentManager().beginTransaction();

            // Иницилазация нового фрагмета
            MainFragment mainFragment = MainFragment.newInstance(OrderFragment.this);
            fTrans.addToBackStack(null);
            fTrans.replace(R.id.fragment_container, mainFragment);
            fTrans.commit();

            // Очистка ненужных более View
            // TODO: При первом запуске приложения без этой строки можно обойтись, но после изменения currentMode, без этой строки не стирается прдыдущий view
            ( (ViewGroup) getActivity().findViewById(R.id.fragment_container) ).removeAllViews();


            /*AddAdresDialogFragment addAdresDialogFragment = AddAdresDialogFragment.newInstance(mTaxiListAdapter);
            addAdresDialogFragment.show(getFragmentManager(), "line width dialog");*/
        }
    };

    View.OnClickListener timeSetClickListener = new View.OnClickListener() {
        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            TimePickerDialogFragment timePickerDialogFragment = TimePickerDialogFragment.newInstance(timeTextView);
            timePickerDialogFragment.show(getFragmentManager(), "l2312");
        }
    };

    /**
     * Слушатель для кнопки перехода к фрагменту {@link SummaryFragment}
     */
    View.OnClickListener nextClickListener = new View.OnClickListener() {
        /**
         * Откывает фрагмента {@link SummaryFragment} при клике
         * @param v {@link View} кнопки, по которой был сделан клик
         */
        @Override
        public void onClick(View v) {
            FragmentTransaction fTrans = getFragmentManager().beginTransaction();

            // Иницилазация нового фрагмета
            SummaryFragment summaryFragment = SummaryFragment.newInstance(adreses);
            fTrans.addToBackStack(null);
            fTrans.replace(R.id.fragment_container, summaryFragment);
            fTrans.commit();

            // Очистка ненужных более View
            // TODO: При первом запуске приложения без этой строки можно обойтись, но после изменения currentMode, без этой строки не стирается прдыдущий view
            ( (ViewGroup) getActivity().findViewById(R.id.fragment_container) ).removeAllViews();
        }
    };
}
