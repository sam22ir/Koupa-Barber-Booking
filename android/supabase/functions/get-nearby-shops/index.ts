import { serve } from "https://deno.land/std@0.168.0/http/server.ts";
import { createClient } from "https://esm.sh/@supabase/supabase-js@2";

serve(async (req: Request) => {
  try {
    const { p_lat, p_lon, p_radius_km, p_wilaya_code } = await req.json();

    if (typeof p_lat !== "number" || typeof p_lon !== "number" || typeof p_radius_km !== "number") {
      return new Response(
        JSON.stringify({ error: "Missing or invalid required parameters: p_lat, p_lon, p_radius_km" }),
        { status: 400, headers: { "Content-Type": "application/json" } }
      );
    }

    const supabase = createClient(
      Deno.env.get("SUPABASE_URL") ?? "",
      Deno.env.get("SUPABASE_SERVICE_ROLE_KEY") ?? ""
    );

    // Use PostGIS ST_DWithin and ST_Distance for geospatial query
    // ST_MakePoint expects (longitude, latitude) order
    // ST_DWithin uses meters, so multiply km by 1000
    const radiusMeters = p_radius_km * 1000;

    let query = supabase.rpc("get_nearby_shops", {
      p_lat,
      p_lon,
      p_radius_meters: radiusMeters,
    });

    if (p_wilaya_code !== undefined && p_wilaya_code !== null) {
      // If wilaya filter is provided, we filter after the RPC call
      // Alternatively, modify the RPC to accept wilaya_code
      const { data, error } = await query;

      if (error) {
        console.error("Error fetching nearby shops:", error);
        return new Response(
          JSON.stringify({ error: error.message }),
          { status: 500, headers: { "Content-Type": "application/json" } }
        );
      }

      const filtered = (data ?? []).filter(
        (shop: Record<string, unknown>) => shop.wilaya_code === p_wilaya_code
      );

      return new Response(JSON.stringify(filtered), {
        headers: { "Content-Type": "application/json" },
      });
    }

    const { data, error } = await query;

    if (error) {
      console.error("Error fetching nearby shops:", error);
      return new Response(
        JSON.stringify({ error: error.message }),
        { status: 500, headers: { "Content-Type": "application/json" } }
      );
    }

    return new Response(JSON.stringify(data ?? []), {
      headers: { "Content-Type": "application/json" },
    });
  } catch (err) {
    console.error("Unexpected error:", err);
    return new Response(
      JSON.stringify({ error: "Internal server error" }),
      { status: 500, headers: { "Content-Type": "application/json" } }
    );
  }
});
