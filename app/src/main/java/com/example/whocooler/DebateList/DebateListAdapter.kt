package com.example.whocooler.DebateList

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ToggleButton
import com.example.whocooler.Common.Models.Debate
import androidx.recyclerview.widget.RecyclerView
import com.example.whocooler.Common.Models.DebateSide
import com.example.whocooler.Common.Models.DebatesResponse
import com.example.whocooler.R
import com.squareup.picasso.Picasso


class DebateListAdapter(
    val context: Context,
    val response: DebatesResponse,
    val voteClick: (Debate, DebateSide) -> Unit,
    val debateClick: (Debate) -> Unit
): RecyclerView.Adapter<DebateListAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        val category = itemView?.findViewById<TextView>(R.id.categoryTextView)
        val leftSideImage = itemView?.findViewById<ImageView>(R.id.listLeftSideImage)
        val leftSideButton = itemView?.findViewById<ToggleButton>(R.id.listLeftSideButton)
        val rightSideImage = itemView?.findViewById<ImageView>(R.id.listRightSideImage)
        val rightSideButton = itemView?.findViewById<ToggleButton>(R.id.listRightSideButton)
        val votesCounter = itemView?.findViewById<TextView>(R.id.listVotesCounter)
        val messageCounter = itemView?.findViewById<TextView>(R.id.listMessageCounter)
        val debateInfoView = itemView?.findViewById<LinearLayout>(R.id.listInfoView)

        fun bindDebate(context: Context, debate: Debate) {
            category?.text = debate.category.name
            leftSideButton?.text = debate.leftSide.name
            rightSideButton?.text = debate.rightSide.name
            votesCounter?.text = debate.votesCount.toString()
            messageCounter?.text = debate.messageCount.toString()
            Picasso.get().load(debate.leftSide.image).into(leftSideImage)
            Picasso.get().load(debate.rightSide.image).into(rightSideImage)

            leftSideButton?.setOnClickListener {
                voteClick(debate, debate.leftSide)
            }

            rightSideButton?.setOnClickListener {
                voteClick(debate, debate.rightSide)
            }

            rightSideImage?.setOnClickListener {
                debateClick(debate)
            }

            leftSideImage?.setOnClickListener {
                debateClick(debate)
            }

            debateInfoView?.setOnClickListener {
                debateClick(debate)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.debate_list_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return response.debates.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
         holder?.bindDebate(context, response.debates[position])
    }



}