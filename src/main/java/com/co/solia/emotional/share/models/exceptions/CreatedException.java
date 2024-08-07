package com.co.solia.emotional.share.models.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Bad request exception.
 *
 * @author luis.bolivar
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class CreatedException extends RuntimeException {
    /**
     * message about the exception.
     */
    private String message;

    /**
     * endpoint where was thrown the exception.
     */
    private String endpoint;

    /**
     * cause of the exception.
     */
    private Throwable cause;
}
