import { serve } from "https://deno.land/std@0.168.0/http/server.ts";
import { createClient } from "https://esm.sh/@supabase/supabase-js@2";

serve(async (req: Request) => {
  try {
    const { p_appointment_id, p_user_id } = await req.json();

    if (!p_appointment_id || !p_user_id) {
      return new Response(
        JSON.stringify({ error: "Missing required parameters: p_appointment_id, p_user_id" }),
        { status: 400, headers: { "Content-Type": "application/json" } }
      );
    }

    const supabase = createClient(
      Deno.env.get("SUPABASE_URL") ?? "",
      Deno.env.get("SUPABASE_SERVICE_ROLE_KEY") ?? ""
    );

    // Step 1: Fetch the appointment with shop info to verify authorization
    const { data: appointment, error: fetchError } = await supabase
      .from("appointments")
      .select("id, customer_id, shop_id, slot_id, status")
      .eq("id", p_appointment_id)
      .single();

    if (fetchError || !appointment) {
      console.error("Appointment not found:", fetchError);
      return new Response(
        JSON.stringify({ error: "Appointment not found" }),
        { status: 404, headers: { "Content-Type": "application/json" } }
      );
    }

    // Step 2: Verify the user is either the customer or the shop owner
    const isCustomer = appointment.customer_id === p_user_id;

    let isShopOwner = false;
    if (!isCustomer) {
      const { data: shop, error: shopError } = await supabase
        .from("barbershops")
        .select("owner_id")
        .eq("id", appointment.shop_id)
        .single();

      if (shopError || !shop) {
        console.error("Shop not found:", shopError);
        return new Response(
          JSON.stringify({ error: "Shop not found" }),
          { status: 404, headers: { "Content-Type": "application/json" } }
        );
      }

      isShopOwner = shop.owner_id === p_user_id;
    }

    if (!isCustomer && !isShopOwner) {
      return new Response(
        JSON.stringify({ error: "Unauthorized: only the customer or shop owner can cancel" }),
        { status: 403, headers: { "Content-Type": "application/json" } }
      );
    }

    // Step 3: Check appointment is cancellable (not already cancelled/completed)
    if (appointment.status === "cancelled" || appointment.status === "completed") {
      return new Response(
        JSON.stringify({ error: `Cannot cancel appointment with status: ${appointment.status}` }),
        { status: 400, headers: { "Content-Type": "application/json" } }
      );
    }

    // Step 4: Cancel the appointment
    const { error: cancelError } = await supabase
      .from("appointments")
      .update({ status: "cancelled" })
      .eq("id", p_appointment_id);

    if (cancelError) {
      console.error("Failed to cancel appointment:", cancelError);
      return new Response(
        JSON.stringify({ error: "Failed to cancel appointment" }),
        { status: 500, headers: { "Content-Type": "application/json" } }
      );
    }

    // Step 5: Free the availability slot
    const { error: slotError } = await supabase
      .from("availability_slots")
      .update({ is_booked: false })
      .eq("id", appointment.slot_id);

    if (slotError) {
      console.error("Failed to free slot:", slotError);
      // Non-critical: appointment is already cancelled, log but don't fail
    }

    return new Response(
      JSON.stringify({ success: true }),
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
