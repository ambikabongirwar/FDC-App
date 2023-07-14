package com.fdcbabulngoapp.loginjavatry

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.NavController
import androidx.navigation.Navigation

class KnowMoreFragment : Fragment() {
    private lateinit var navController: NavController
    private lateinit var aboutUsButton: Button
    private lateinit var objectiveButton: Button
    private lateinit var missionButton: Button
    private lateinit var contactButton: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        aboutUsButton = view.findViewById(R.id.about)
        aboutUsButton.setOnClickListener {
            navController.navigate(R.id.action_knowMoreFragment_to_aboutFragment)
        }

        objectiveButton = view.findViewById(R.id.objective)
        objectiveButton.setOnClickListener {
            navController.navigate(R.id.action_knowMoreFragment_to_objectiveFragment)
        }

        missionButton = view.findViewById(R.id.mission)
        missionButton.setOnClickListener {
            navController.navigate(R.id.action_knowMoreFragment_to_missionFragment)
        }

        contactButton = view.findViewById(R.id.contact)
        contactButton.setOnClickListener {
            navController.navigate(R.id.action_knowMoreFragment_to_contactUsFragment)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_know_more, container, false)


        return view
    }

}