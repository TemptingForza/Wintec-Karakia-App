package com.group3.karakiaapp.fragments;

import android.content.Context;
import android.hardware.input.InputManager;
import android.inputmethodservice.Keyboard;
import android.net.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import static com.group3.karakiaapp.MainActivity.Utils.*;

import com.group3.karakiaapp.*;
import com.group3.karakiaapp.R;

import java.util.*;
import java.util.function.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.helper.widget.*;
import androidx.constraintlayout.widget.*;
import androidx.fragment.app.*;
import androidx.navigation.*;

public class SearchFragment extends FragmentBase {
    Switch titles;
    Switch words;
    ConstraintLayout container;
    Flow containerFlow;
    View self;
    NavController nav;
    EditText searchBar;
    ArrayList<Integer> search = new ArrayList<>();

    public SearchFragment() { super(R.layout.fragment_search); }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        self = view;
        nav = Navigation.findNavController(view);
        container = view.findViewById(R.id.container_karakias);
        containerFlow = view.findViewById(R.id.flow_karakias);
        titles = view.findViewById(R.id.switch_titles);
        titles.setChecked(true);
        words = view.findViewById(R.id.switch_words);
        searchBar = view.findViewById(R.id.input_search);
        View searchButton = view.findViewById(R.id.button_search);
        searchButton.setOnClickListener((x) -> {
            HideKeyboard();
            String term = searchBar.getText().toString();
            if (term == null || term.length() == 0 || IsWhitespace(term))
            {
                Log.e("search","Empty search term. Search canceled");
                return;
            }
            boolean checkNames = titles.isChecked();
            boolean checkWords = words.isChecked();
            searchButton.setEnabled(false);
            ResourceManager.Instance().RunAsync(() -> {
                List<SearchResult> values = new ArrayList<>();
                for (Karakia song: ResourceManager.Instance().karakias.values()) {
                    float value = SearchValue(song,term,checkNames,checkWords);
                    if (song.id != 0 && value > 0)
                        values.add(new SearchResult(song,value));
                }
                values.sort((y,z) -> Float.compare(y.Value,z.Value));
                search.clear();
                container.post(() -> ClearItems());
                for (SearchResult s : values)
                {
                    search.add(s.Karakia.id);
                    container.post(() -> AddItem(s.Karakia));
                }
                searchButton.post(() -> searchButton.setEnabled(true));
            });
        });
        MainActivity.instance.TryOpenTorial(ResourceManager.Instance().helpVideos.get("search"));
    }
    class SearchResult {
        float Value;
        Karakia Karakia;
        public SearchResult(Karakia karakia, float value) {
            Karakia = karakia;
            Value = value;
        }
    }

    static void HideKeyboard() {
        ((InputMethodManager)MainActivity.instance.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(MainActivity.instance.getCurrentFocus().getWindowToken(),0);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("titles_checked",titles.isChecked());
        outState.putBoolean("words_checked",words.isChecked());
        outState.putIntegerArrayList("search_results",search);
        outState.putString("search_text",searchBar.getText().toString());
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        TryGetValue(savedInstanceState,"titles_checked",(Boolean x) -> titles.setChecked(x));
        TryGetValue(savedInstanceState,"words_checked",(Boolean x) -> words.setChecked(x));
        TryGetValue(savedInstanceState,"search_results",(ArrayList<Integer> x) -> {
            search = x;
            for (Integer id: search)
                  AddItem(ResourceManager.Instance().karakias.get(id));
        });
        TryGetValue(savedInstanceState,"search_text",(String x) -> searchBar.setText(x));
    }

    public CardView AddItem(Karakia song) {
        CardView card = (CardView)View.inflate(self.getContext(), R.layout.item_karakia, new CardView(self.getContext()));
        ((TextView)card.findViewById(R.id.text_karakia)).setText(song.name);
        ((ImageView)card.findViewById(R.id.image_karakia)).setImageDrawable(song.image);
        card.setId(View.generateViewId());
        container.addView(card);
        containerFlow.addView(card);
        card.setOnClickListener((x) -> {
            HideKeyboard();
            nav.navigate(SearchFragmentDirections.actionSearchFragmentToKarakiaFragment(song.id));
        });
        return card;
    }

    public void ClearItems() {
        for (int id : containerFlow.getReferencedIds()) {
            View v = container.findViewById(id);
            containerFlow.removeView(v);
            container.removeView(v);
        }
    }

    static final float searchCasePenalty = 0.9f;
    static final float searchFullMatch = 2;
    static final float searchContainsMatch = 1.5f;
    static float SearchValue(Karakia karakia, String term, boolean checkName, boolean checkWords) {
        if (!checkName && !checkWords)
            return 0f;
        if (checkName && karakia.name.equals(term))
            return searchFullMatch;
        if (checkName && karakia.name.equalsIgnoreCase(term))
            return searchFullMatch * searchCasePenalty;
        if (checkName && karakia.name.contains(term))
            return searchContainsMatch;
        String[] words = checkWords ? new String[]{karakia.wordsEnglish, karakia.wordsMaori} : null;
        if (checkWords && Any(words,(x) -> x.contains(term)))
            return searchContainsMatch;
        String lowerTerm = term.toLowerCase();
        String lowerName = checkName ? karakia.name.toLowerCase() : null;
        String[] lowerWords = checkWords ? new String[words.length] : null;
        if (checkWords) {
            for (int i = 0; i < words.length; i++)
                lowerWords[i] = words[i].toLowerCase();
            if (Any(lowerWords,(x) -> x.contains(lowerTerm)))
                return searchContainsMatch * searchCasePenalty;
        }
        List<String> terms = GetTerms(term);
        List<String> lowerTerms = GetTerms(lowerTerm);
        float size = 0;
        for (String item: terms)
            size += item.length();
        float total = 0;
        for (int i = 0; i < terms.size(); i++)
        {
            String t = terms.get(i);
            String lT = lowerTerms.get(i);
            if ((checkName && karakia.name.contains(t)) || (checkWords && Any(words,(x) -> x.contains(t))))
                total += t.length() / size;
            else if ((checkName && lowerName.contains(lT)) || (checkWords && Any(lowerWords,(x) -> x.contains(lT))))
                total += t.length() / size * searchCasePenalty;
        }
        return total;
    }
    static List<String> GetTerms(String fullTerm) {
        List<String> terms = new ArrayList<>();
        String current = "";
        for (int i = 0; i < fullTerm.length();i++)
            if (!Character.isWhitespace(fullTerm.charAt(i)))
                current += fullTerm.charAt(i);
            else if (current.length() > 0)
            {
                terms.add(current);
                current = "";
            }
        if (current.length() > 0)
            terms.add(current);
        return terms;
    }
}
