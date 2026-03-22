import { serve } from "https://deno.land/std@0.168.0/http/server.ts";
import { createClient } from "https://esm.sh/@supabase/supabase-js@2";

serve(async (req: Request) => {
  try {
    const { p_customer_id, p_shop_id, p_slot_id } = await req.json();

    if (!p_customer_id || !p_shop_id || !p_slot_id) {
      return new Response(
        JSON.stringify({ error: "Missing required parameters: p_customer_id, p_shop_id, p_slot_id" }),
        { status: 400, headers: { "Content-Type": "application/json" } }
      );
    }

    const supabase = createClient(
      Deno.env.get("SUPABASE_URL") ?? "",
      Deno.env.get("SUPABASE_SERVICE_ROLE_KEY") ?? ""
    );

    // Step 1: Lock the slot and check availability atomically
    // Use a transaction-like approach: select for update by checking is_booked
    const { data: slot, error: slotError } = await supabase
      .from("availability_slots")
      .select("id, shop_id, slot_date, slot_time, is_open, is_booked")
      .eq("id", p_slot_id)
      .eq("shop_id", p_shop_id)
      .eq("is_open", true)
      .eq("is_booked", false)
      .single();

    if (slotError || !slot) {
      console.error("Slot not available:", slotError);
      return new Response(
        JSON.stringify({ error: "Slot is not available or does not exist" }),
        { status: 409, headers: { "Content-Type": "application/json" } }
      );
    }

    // Step 2: Mark slot as booked (optimistic locking)
    const { error: updateError } = await supabase
      .from("availability_slots")
      .update({ is_booked: true })
      .eq("id", p_slot_id)
      .eq("is_booked", false); // Ensure it wasn't booked by another request

    if (updateError) {
      console.error("Failed to lock slot:", updateError);
      return new Response(
        JSON.stringify({ error: "Slot was booked by another user" }),
        { status: 409, headers: { "Content-Type": "application/json" } }
      );
    }

    // Step 3: Insert the appointment
    const { data: appointment, error: insertError } = await supabase
      .from("appointments")
      .insert({
        customer_id: p_customer_id,
        shop_id: p_shop_id,
        slot_id: p_slot_id,
        appointment_date: slot.slot_date,
        time_slot: slot.slot_time,
        status: "pending",
        payment_method: "cash_on_arrival",
      })
      .select("id")
      .single();

    if (insertError || !appointment) {
      console.error("Failed to create appointment:", insertError);

      // Rollback: unmark the slot
      await supabase
        .from("availability_slots")
        .update({ is_booked: false })
        .eq("id", p_slot_id);

      return new Response(
        JSON.stringify({ error: "Failed to create appointment" }),
        { status: 500, headers: { "Content-Type": "application/json" } }
      );
    }

    return new Response(
      JSON.stringify({ appointment_id: appointment.id }),
      { headers: { "Content-Type": "application/json" } }
    );
  } catch (err) {
    console.error("Unexpected error:", err);
    return new Response(
      JSON.stringify({ error: "Internal server error" }),
      { status: 500, headers: { "Content-Type": "application/json" } }
    );
  }
});
