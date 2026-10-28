package com.example.smartpantrymanager;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    private Context context;
    private Cursor cursor;

    public PantryAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pantry, parent, false);
        return new PantryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        if (!cursor.moveToPosition(position)) {
            return;
        }

        int idIndex = cursor.getColumnIndex(DatabaseHelper.COL_ID);
        int nameIndex = cursor.getColumnIndex(DatabaseHelper.COL_NAME);
        int qtyIndex = cursor.getColumnIndex(DatabaseHelper.COL_QTY);
        int unitIndex = cursor.getColumnIndex(DatabaseHelper.COL_UNIT);
        int expiryIndex = cursor.getColumnIndex(DatabaseHelper.COL_EXPIRY);

        final long id = cursor.getLong(idIndex);
        String name = cursor.getString(nameIndex);
        double qty = cursor.getDouble(qtyIndex);
        String unit = cursor.getString(unitIndex);
        String expiry = cursor.getString(expiryIndex);

        holder.tvItemName.setText(name);

        String qtyText = "Qty: " + qty + " " + (unit != null ? unit : "");
        if (expiry != null && !expiry.isEmpty()) {
            qtyText += " | Exp: " + expiry;
        }
        holder.tvItemQty.setText(qtyText);

        // Open the Add/Edit screen when the user clicks on the item row
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddIngredientActivity.class);
            intent.putExtra("EDIT_ID", id);
            intent.putExtra("EDIT_NAME", name);
            intent.putExtra("EDIT_QTY", qty);
            intent.putExtra("EDIT_UNIT", unit);
            intent.putExtra("EDIT_EXPIRY", expiry);
            context.startActivity(intent);
        });

        // Delete Button Logic
        holder.btnDelete.setOnClickListener(v -> {
            DatabaseHelper dbHelper = new DatabaseHelper(context);
            dbHelper.getWritableDatabase().delete(
                    DatabaseHelper.TABLE_PANTRY,
                    DatabaseHelper.COL_ID + " = ?",
                    new String[]{String.valueOf(id)}
            );

            // Refresh the list
            swapCursor(new DatabaseHelper(context).getReadableDatabase().query(
                    DatabaseHelper.TABLE_PANTRY, null, null, null, null, null, null
            ));
        });
    }

    @Override
    public int getItemCount() {
        return cursor == null ? 0 : cursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        if (cursor != null) {
            cursor.close();
        }
        cursor = newCursor;
        if (newCursor != null) {
            notifyDataSetChanged();
        }
    }

    public static class PantryViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName, tvItemQty;
        Button btnDelete;

        public PantryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tvItemName);
            tvItemQty = itemView.findViewById(R.id.tvItemQty);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}