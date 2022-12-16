-- Gets all the latest counts for each contention_id
CREATE OR REPLACE FUNCTION GetLatestCounts() returns setof jsonb
AS $function$
BEGIN
Return query
SELECT DISTINCT ON (contention_id) evidence_count_summary
FROM   claims.assessment_result
ORDER  BY contention_id, created_at DESC NULLS LAST, contention_id;
END;
$function$ LANGUAGE plpgsql;
