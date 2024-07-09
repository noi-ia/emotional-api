package com.co.solia.emotional.campaign.models.daos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document("keyphrase")
public class CampaignDao {

    /**
     * identifier.
     */
    @Id
    private UUID id;

    /**
     * keyphrase associated with the campaign.
     */
    private String keyphrase;

    /**
     * emotions id for emotions processing.
     */
    private UUID emotionsId;

    /**
     * user identifier that start emotional estimation.
     */
    private UUID idUser;

    /**
     * tokens of message representing.
     */
    private Map<String, Integer> tokens;

    /**
     * is active this processing to still reviewing.
     */
    @Builder.Default
    private Boolean activate = Boolean.TRUE;

    /**
     * date of emotional estimation created.
     */
    @Builder.Default
    private long created = Instant.now().getEpochSecond();

    /**
     * the campaign json structure,
     */
    private String campaign;

    /**
     * brand identifier associated.
     */
    private UUID brandId;

    /**
     * duration of emotional estimation.
     */
    private long duration;

    /**
     * processing id from openai.
     */
    private String openAiId;

    /**
     * system fingerprint from openai result.
     */
    private String fingerPrintOpenai;
}
