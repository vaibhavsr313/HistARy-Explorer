package com.example.a;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import nl.dionsegijn.konfetti.core.PartyFactory;
import nl.dionsegijn.konfetti.core.Position;
import nl.dionsegijn.konfetti.core.emitter.Emitter;
import nl.dionsegijn.konfetti.core.models.Shape;
import nl.dionsegijn.konfetti.core.models.Size;
import nl.dionsegijn.konfetti.xml.KonfettiView;

public class PlaceBottomSheetFragment extends BottomSheetDialogFragment {

    private String url = "";

    private boolean isLikeProgrammaticChange = false;
    private boolean isVisitedProgrammaticChange = false;

    private static final String ARG_AR = "ARModel";
    private static final String ARG_ID = "ID";
    private static final String ARG_NAME = "name";
    private static final String ARG_INFO = "info";
    private static final String ARG_HISTORY = "history";
    private static final String ARG_IMAGE_URLS = "image_urls";
    private static final String ARG_FACTS = "facts";
    private static final String ARG_QUESTION = "question";
    private static final String ARG_OPTIONS = "options";
    private static final String ARG_ANSWER = "correct_answer";

    private String placeId,name, info, history, question, correctAnswer;
    private List<String> imageUrls, facts, options;

    private ViewPager2 imageSlider;
    private ImageView leftArrow, rightArrow;
    private TabLayout imageSliderIndicator;
    KonfettiView konfettiView;
    ToggleButton like_button,visited_button;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    // Required empty constructor
    public PlaceBottomSheetFragment() {
    }

    // Static factory method to create a new instance with arguments
    public static PlaceBottomSheetFragment newInstance(String placeId,String name, String info, String history,
                                                       List<String> imageUrls, List<String> facts,
                                                       String question, List<String> options, String correctAnswer,String url) {
        PlaceBottomSheetFragment fragment = new PlaceBottomSheetFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ID,placeId);
        args.putString(ARG_NAME, name);
        args.putString(ARG_INFO, info);
        args.putString(ARG_HISTORY, history);
        args.putStringArrayList(ARG_IMAGE_URLS, new ArrayList<>(imageUrls));
        args.putStringArrayList(ARG_FACTS, new ArrayList<>(facts));
        args.putString(ARG_QUESTION, question);
        args.putStringArrayList(ARG_OPTIONS, new ArrayList<>(options));
        args.putString(ARG_ANSWER, correctAnswer);
        args.putString(ARG_AR,url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            placeId = getArguments().getString(ARG_ID);
            name = getArguments().getString(ARG_NAME);
            info = getArguments().getString(ARG_INFO);
            history = getArguments().getString(ARG_HISTORY);
            imageUrls = getArguments().getStringArrayList(ARG_IMAGE_URLS);
            facts = getArguments().getStringArrayList(ARG_FACTS);
            question = getArguments().getString(ARG_QUESTION);
            options = getArguments().getStringArrayList(ARG_OPTIONS);
            correctAnswer = getArguments().getString(ARG_ANSWER);
            url = getArguments().getString(ARG_AR);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_place_bottom_sheet, container, false);

        // Initialize views
        TextView placeName = view.findViewById(R.id.place_name);
        TextView placeInfo = view.findViewById(R.id.place_info);
        TextView historyInfo = view.findViewById(R.id.history_info);
        ViewPager2 imageSlider = view.findViewById(R.id.image_slider);
        RecyclerView factsRecyclerView = view.findViewById(R.id.facts_recycler);
        Button triviaButton = view.findViewById(R.id.trivia_button);
        imageSlider = view.findViewById(R.id.image_slider); // Changed: ImageSlider is initialized here.
        imageSliderIndicator = view.findViewById(R.id.image_slider_indicator);
        Button navigate_here = view.findViewById(R.id.navigate_here);
        Button threeD_button = view.findViewById(R.id.threeD_button);
        like_button = view.findViewById(R.id.like_button);
        visited_button = view.findViewById(R.id.visited_button);
        konfettiView = view.findViewById(R.id.konfettiView);

        threeD_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String uri = "https://arvr.google.com/scene-viewer/1.0?file=";
                String model = uri + url;
                Intent sceneViewerIntent = new Intent(Intent.ACTION_VIEW);
                sceneViewerIntent.setData(Uri.parse(model));
                sceneViewerIntent.setPackage("com.google.android.googlequicksearchbox");
                startActivity(sceneViewerIntent);

            }
        });

        //check the status of like button after showing the bottom sheet
        likeCheckStatus();

        //check the status of visit button after showing the bottom sheet
        visitCheckStatus();

        visited_button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isVisitedProgrammaticChange) {
                    return; // Ignore programmatic changes
                }

                if(isChecked)
                {
                    uploadVisitedData();
                    showVisitedKonfetti();
                    //Toast.makeText(requireContext(), "You Visited it", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    removeVisitedData();
                    //Toast.makeText(requireContext(), "You Unvisited it", Toast.LENGTH_SHORT).show();
                }

            }
        });

        like_button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isLikeProgrammaticChange) {
                    return; // Ignore programmatic changes
                }


                if(isChecked)
                {
                    uploadLikeData();
                    showlikeKonfetti();
                    //Toast.makeText(requireContext(), "You Liked it", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    removeLikeData();
                    //Toast.makeText(requireContext(), "You Unliked it", Toast.LENGTH_SHORT).show();
                }

            }
        });


        // Set data
        placeName.setText(name);
        placeInfo.setText(info);
        historyInfo.setText(history);

        // Set up image slider
        ImageSliderAdapter imageAdapter = new ImageSliderAdapter(imageUrls);
        imageSlider.setAdapter(imageAdapter);

        factsRecyclerView.setNestedScrollingEnabled(false);
        factsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        factsRecyclerView.setAdapter(new FactsAdapter(facts));

        triviaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), TriviaActivity.class);
                intent.putExtra(ARG_QUESTION, question);
                intent.putStringArrayListExtra(ARG_OPTIONS, new ArrayList<>(options));
                intent.putExtra(ARG_ANSWER, correctAnswer);

                // Start TriviaActivity
                startActivity(intent);
            }
        });


        // Set up image slider
        ImageSliderAdapter imageSliderAdapter = new ImageSliderAdapter(imageUrls);
        imageSlider.setAdapter(imageSliderAdapter);

        // Link TabLayout with ViewPager2
        new TabLayoutMediator(imageSliderIndicator, imageSlider, (tab, position) -> {
            // Tab Configuration if required
        }).attach();

        return view;
    }

    private void visitCheckStatus(){

        if (user != null) {

            String userId = user.getUid();

            FirebaseDatabase.getInstance().getReference("Users_Likes")
                    .child(userId)
                    .child(placeId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            isVisitedProgrammaticChange = true;
                            if(snapshot.exists())
                            {
                                //like_button is liked
                                visited_button.setChecked(true);
                            }
                            else
                            {
                                //like button is not liked
                                visited_button.setChecked(false);
                            }
                            isVisitedProgrammaticChange = false;
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(requireContext(), "Failed to fetch visit status", Toast.LENGTH_SHORT).show();
                        }
                    });

        }

    }

    private void removeVisitedData(){

        if (user != null) {

            String userId = user.getUid();

            FirebaseDatabase.getInstance().getReference("Users_Visits")
                    .child(userId)
                    .child(placeId)
                    .removeValue()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful())
                            {
                                //Toast.makeText(requireContext(), "Visit removed", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(requireContext(), "Failed to remove Visit", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(requireContext(), "Failed Visiting", Toast.LENGTH_SHORT).show();
                        }
                    });

        }

    }

    private void uploadVisitedData(){

        if (user != null) {

            String userId = user.getUid();


            FirebaseDatabase.getInstance().getReference("Users_Visits")
                    .child(userId)
                    .child(placeId)
                    .setValue(true)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                //Toast.makeText(requireContext(), "Visited Successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(requireContext(), "Failed to Visit Place", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(requireContext(), "Failed Saving Visit Status", Toast.LENGTH_SHORT).show();
                        }
                    });

        }

    }

    private void showVisitedKonfetti()
    {
        // Show confetti when "Liked"
        konfettiView.bringToFront();
        konfettiView.start(
                new PartyFactory(new Emitter(3, TimeUnit.SECONDS).perSecond(200)) // Higher emission rate for dense effect
                        .angle(270) // Emit upwards
                        .spread(180) // Full spread for wide coverage
                        .setSpeedBetween(8f, 16f) // Adjusted speed for more energy
                        .timeToLive(2000) // Particles last for 3 seconds
                        .shapes(Arrays.asList(Shape.Square.INSTANCE, Shape.Circle.INSTANCE)) // Multiple shapes for variation
                        .sizes(new Size(12, 0.5f, 20f), new Size(16, 0.8f, 25f)) // Three parameters: size, mass, and maxSize
                        .colors(Arrays.asList(0xfce18a, 0xff726d, 0xf4306d, 0xb48def, 0x8fd3f4, 0x80ed99)) // Vibrant colors
                        .position(new Position.Relative(0.5, 0.6)) // Confetti appears in the center
                        .build()
        );


    }

    private void likeCheckStatus(){

        if (user != null) {

            String userId = user.getUid();

            FirebaseDatabase.getInstance().getReference("Users_Likes")
                    .child(userId)
                    .child(placeId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            isLikeProgrammaticChange = true;
                            if(snapshot.exists())
                            {
                                //like_button is liked
                                like_button.setChecked(true);
                            }
                            else
                            {
                                //like button is not liked
                                like_button.setChecked(false);
                            }
                            isLikeProgrammaticChange = false;
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(requireContext(), "Failed to fetch like status", Toast.LENGTH_SHORT).show();
                        }
                    });

        }

    }

    private void removeLikeData(){

        if (user != null) {

            String userId = user.getUid();

            FirebaseDatabase.getInstance().getReference("Users_Likes")
                    .child(userId)
                    .child(placeId)
                    .removeValue()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful())
                            {
                                //Toast.makeText(requireContext(), "Liked removed", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(requireContext(), "Failed to remove like", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(requireContext(), "Failed Liking", Toast.LENGTH_SHORT).show();
                        }
                    });

        }

    }

    private void uploadLikeData(){

        if (user != null) {

            String userId = user.getUid();


            FirebaseDatabase.getInstance().getReference("Users_Likes")
                    .child(userId)
                    .child(placeId)
                    .setValue(true)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                //Toast.makeText(requireContext(), "Liked Succesfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(requireContext(), "Failed to Like Place", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(requireContext(), "Failed Saving like status", Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }

    private void showlikeKonfetti() {
        // Show confetti when "Liked"
        konfettiView.bringToFront();
        konfettiView.start(
                new PartyFactory(new Emitter(2, TimeUnit.SECONDS).perSecond(50))
                        .spread(360)
                        .colors(Arrays.asList(0xfce18a, 0xff726d, 0xf4306d, 0xb48def))
                        .build()
        );
    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        // Apply rounded corners to the BottomSheet itself
        dialog.setOnShowListener(dialogInterface -> {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
            View bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);

            if (bottomSheet != null) {
                bottomSheet.setBackgroundResource(R.drawable.rounded_top_black_background);
            }
        });

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        if (view != null) {
            View parent = (View) view.getParent();
            BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(parent);

            // Set initial height to half-screen
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            behavior.setPeekHeight(getResources().getDisplayMetrics().heightPixels / 2);

            // Allow full expansion when scrolling
            behavior.setFitToContents(false);
            behavior.setHalfExpandedRatio(0.5f);
        }
    }
}
