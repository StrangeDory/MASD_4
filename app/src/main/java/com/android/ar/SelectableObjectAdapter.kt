package com.android.ar

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.android.ar.databinding.ItemSelectableObjectBinding

class SelectableObjectAdapter(
	private val objects: List<SelectableObject>,
	private val itemClickListener: Listener,
) :
	RecyclerView.Adapter<SelectableObjectAdapter.ViewHolder>() {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		val view = LayoutInflater.from(parent.context)
			.inflate(R.layout.item_selectable_object, parent, false)
		return ViewHolder(view)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		holder.bind(objects[position])
		Log.d("Adapter", objects[position].name.toString())
	}

	override fun getItemCount() = objects.size

	inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
		private val binding by viewBinding(ItemSelectableObjectBinding::bind)

		init {
			binding.root.setOnClickListener {
				itemClickListener.onClick(bindingAdapterPosition)
			}
		}

		fun bind(obj: SelectableObject) {
			with(binding) {
				tvName.text = obj.name
				ivImage.setImageResource(obj.image)
			}
		}
	}

}