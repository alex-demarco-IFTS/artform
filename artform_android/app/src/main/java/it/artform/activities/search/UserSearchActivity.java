package it.artform.activities.search;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.List;

import it.artform.AFGlobal;
import it.artform.activities.notifications.NotificationsActivity;
import it.artform.activities.publication.ContentPubActivity;
import it.artform.activities.profile.ExternalProfileActivity;
import it.artform.activities.homepage.MainActivity;
import it.artform.R;
import it.artform.activities.profile.UserProfileActivity;
import it.artform.feed.UserGridAdapter;
import it.artform.pojos.Topic;
import it.artform.pojos.User;
import it.artform.web.ArtformApiEndpointInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserSearchActivity extends Activity {
    SearchView contentSearchView = null;
    Button searchArtworksButton = null;
    Button searchVideosButton = null;
    Button searchArtistsButton = null;
    Spinner topicSpinner = null;
    GridView artistsGridView = null;
    TextView noResultTextView = null;
    BottomNavigationView bottomNavigationView = null;

    ArtformApiEndpointInterface apiService = null;

    String selectedTopic = null;
    String[] searchedUsers = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);

        //widget setup
        contentSearchView = findViewById(R.id.contentSearchView); //search while typing
        contentSearchView.setQueryHint("Search…");
        searchArtworksButton = findViewById(R.id.searchArtworksButton); //no listener
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            searchArtworksButton.setBackgroundColor(UserSearchActivity.this.getColor(R.color.white));
        }
        searchVideosButton = findViewById(R.id.searchVideosButton); //activity
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            searchVideosButton.setBackgroundColor(UserSearchActivity.this.getColor(R.color.white));
        }
        searchArtistsButton = findViewById(R.id.searchArtistsButton); //activity
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            searchArtistsButton.setBackgroundColor(UserSearchActivity.this.getColor(R.color.purple_500));
        }
        topicSpinner = findViewById(R.id.topicSpinner); //fetch topics
        artistsGridView = findViewById(R.id.artistsGridView); //load posts
        noResultTextView = findViewById(R.id.noResultTextView); //show only when no posts
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(navListener);
        bottomNavigationView.getMenu().getItem(1).setChecked(true);

        //web services setup
        AFGlobal app = (AFGlobal) getApplication();
        apiService = app.retrofit.create(ArtformApiEndpointInterface.class);

        //GET dei Topic
        fetchTopics();

        //imposta SearchView da parametri
        Bundle searchParams = getIntent().getExtras();
        if(searchParams != null)
            contentSearchView.setQuery(searchParams.getCharSequence("keywords"), false);

        //listener barra di ricerca
        contentSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                fetchUsers((selectedTopic == null ? "" : selectedTopic), String.valueOf(contentSearchView.getQuery()));
                return true;
            }
        });

        //listener pulsante Opere
        searchArtworksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent artworkSearchIntent = new Intent(UserSearchActivity.this, ImagePostSearchActivity.class);
                if(selectedTopic != null)
                    artworkSearchIntent.putExtra("topic", selectedTopic);
                artworkSearchIntent.putExtra("keywords", contentSearchView.getQuery());
                startActivity(artworkSearchIntent);
            }
        });

        //listener pulsante Video
        searchVideosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent videoSearchIntent = new Intent(UserSearchActivity.this, VideoPostSearchActivity.class);
                if(selectedTopic != null)
                    videoSearchIntent.putExtra("topic", selectedTopic);
                videoSearchIntent.putExtra("keywords", contentSearchView.getQuery());
                startActivity(videoSearchIntent);
            }
        });
    }

    // listener NavigationBar
    private NavigationBarView.OnItemSelectedListener navListener = new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch(item.getItemId()) {
                case R.id.home_item:
                    Intent homeIntent = new Intent(UserSearchActivity.this, MainActivity.class);
                    startActivity(homeIntent);
                    break;
                case R.id.search_item:
                    break;
                case R.id.add_item:
                    Intent publishIntent = new Intent(UserSearchActivity.this, ContentPubActivity.class);
                    startActivity(publishIntent);
                    break;
                case R.id.notifications_item:
                    Intent notificationsIntent = new Intent(UserSearchActivity.this, NotificationsActivity.class);
                    startActivity(notificationsIntent);
                    break;
                case R.id.profile_item:
                    Intent userProfileIntent = new Intent(UserSearchActivity.this, UserProfileActivity.class);
                    startActivity(userProfileIntent);
                    break;
            }
            return false;
        }
    };

    private void fetchTopics() {
        Call<List<Topic>> getTopicsCall = apiService.getAllTopics();
        getTopicsCall.enqueue(new Callback<List<Topic>>() {
            @Override
            public void onResponse(Call<List<Topic>> call, Response<List<Topic>> response) {
                if(response.isSuccessful()) {
                    String[] topics = new String[response.body().size() + 1];
                    topics[0] = "Select topic:";
                    for(int i=1; i<topics.length; i++)
                        topics[i] = response.body().get(i-1).getName();
                    ArrayAdapter topicsAdapter = new ArrayAdapter<String>(UserSearchActivity.this, android.R.layout.simple_spinner_dropdown_item, topics);
                    topicSpinner.setAdapter(topicsAdapter);
                    //imposta Spinner da parametri
                    Bundle searchParams = getIntent().getExtras();
                    if(searchParams != null) {
                        String topicParam = searchParams.getString("topic");
                        if(topicParam != null) {
                            selectedTopic = topicParam;
                            topicSpinner.setSelection(topicsAdapter.getPosition(selectedTopic));
                        }
                    }
                    topicSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            if(adapterView.getItemAtPosition(i).equals("Select topic:"))
                                selectedTopic = null;
                            else
                                selectedTopic = String.valueOf(adapterView.getItemAtPosition(i));
                            if(!String.valueOf(contentSearchView.getQuery()).equals(""))
                                fetchUsers((selectedTopic == null ? "" : selectedTopic), String.valueOf(contentSearchView.getQuery()));
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                        }
                    });
                }
                else
                    Toast.makeText(UserSearchActivity.this, "Error while fetching topics: ERROR " + response.code(), Toast.LENGTH_LONG).show();
            }
            @Override
            public void onFailure(Call<List<Topic>> call, Throwable t) {
                Toast.makeText(UserSearchActivity.this, "Error while fetching topics: " + t.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void fetchUsers(String topic, String keywords) {
        Call<List<User>> getUsersByFiltersCall = apiService.getUsersByFilters(topic, keywords);
        getUsersByFiltersCall.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if(response.isSuccessful()) {
                    searchedUsers = new String[response.body().size()];
                    for(int i=0; i<searchedUsers.length; i++)
                        searchedUsers[i] = response.body().get(i).getUsername();
                    //Caricamento utenti nella GridView
                    if(searchedUsers.length > 0) {
                        noResultTextView.setVisibility(View.INVISIBLE);
                        artistsGridView.setAdapter(new UserGridAdapter(UserSearchActivity.this, R.layout.item_user_grid, searchedUsers));
                        //dettagli profilo utente
                        artistsGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                                //se l'utente selezionato corrisponde al proprio profilo
                                if(searchedUsers[position].equals(AFGlobal.getLoggedUser())) {
                                    Intent userProfileIntent = new Intent(UserSearchActivity.this, UserProfileActivity.class);
                                    startActivity(userProfileIntent);
                                    return;
                                }
                                Intent externalProfileIntent = new Intent(UserSearchActivity.this, ExternalProfileActivity.class);
                                externalProfileIntent.putExtra("username", searchedUsers[position]);
                                startActivity(externalProfileIntent);
                            }
                        });
                    }
                    else
                        noResultTextView.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                noResultTextView.setVisibility(View.VISIBLE);
                Toast.makeText(UserSearchActivity.this, "Error while searching users: " + t.toString(), Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }

}