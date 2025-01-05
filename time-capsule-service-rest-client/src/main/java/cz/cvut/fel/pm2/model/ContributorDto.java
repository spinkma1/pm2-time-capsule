package cz.cvut.fel.pm2.model;

import java.util.List;

/**
 * Data transfer object representing a contributor.
 *
 * @param email the email address of the contributor
 */
public record ContributorDto (
    String email
)
{}
