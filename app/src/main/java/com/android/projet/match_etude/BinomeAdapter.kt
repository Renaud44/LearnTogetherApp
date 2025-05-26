package com.android.projet.match_etude

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.projet.R
import com.android.projet.models.Binome

class BinomeAdapter(
    private val binomes: MutableList<Binome>,
    private val onAccept: (Binome) -> Unit,
    private val onRefuse: (Binome) -> Unit
) : RecyclerView.Adapter<BinomeAdapter.BinomeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BinomeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_binome, parent, false)
        return BinomeViewHolder(view)
    }

    override fun onBindViewHolder(holder: BinomeViewHolder, position: Int) {
        val binome = binomes[position]
        holder.pseudo.text = binome.pseudo
        holder.matieresDifficiles.text = "Difficultés : ${binome.matieresDifficiles ?: "N/A"}"
        holder.matieresMaitrisees.text = "Maîtrisées : ${binome.matieresMaitrisees ?: "N/A"}"

        holder.btnAccepter.setOnClickListener {
            onAccept(binome)
        }

        holder.btnRefuser.setOnClickListener {
            binomes.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, binomes.size)
            onRefuse(binome)
        }
    }

    override fun getItemCount(): Int = binomes.size

    class BinomeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val pseudo: TextView = view.findViewById(R.id.pseudo)
        val matieresDifficiles: TextView = view.findViewById(R.id.textMatieresDifficiles)
        val matieresMaitrisees: TextView = view.findViewById(R.id.textMatieresMaitrisees)
        val btnAccepter: Button = view.findViewById(R.id.buttonAccepter)
        val btnRefuser: Button = view.findViewById(R.id.buttonRefuser)
    }
}
