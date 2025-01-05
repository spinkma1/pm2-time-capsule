describe('Dashboard Page Tests', () => {
    beforeEach(() => {
        // Mock login and token setup
        cy.intercept('POST', '/user/login', {
            statusCode: 200,
            body: {
                message: 'Login successful',
                email: 'test@example.com',
                token: 'mockedToken',
            },
        }).as('login');

        cy.intercept('POST', '/capsules/user', {
            statusCode: 200,
            body: [
                {
                    id: "1",
                    name: "Time Capsule 2025",
                    description: "A capsule containing memories and files for the future.",
                    createdDate: "2024-12-31T10:00:00.000Z",
                    unlockTime: "2025-12-31T10:00:00.000Z",
                    state: "editing",
                    users: [
                        { id: "101", name: "Alice", email: "alice@example.com" },
                        { id: "102", name: "Bob", email: "bob@example.com" }
                    ],
                    content: [
                        {
                            id: "201",
                            name: "Vacation Photo",
                            dataType: "image",
                            thumbnail: "https://via.placeholder.com/150",
                            addedBy: "Alice",
                            addedDate: "2024-11-30T12:00:00.000Z"
                        },
                        {
                            id: "202",
                            name: "Graduation Speech",
                            dataType: "text",
                            content: "This is the speech content...",
                            addedBy: "Bob",
                            addedDate: "2024-12-01T15:00:00.000Z"
                        },
                        {
                            id: "203",
                            name: "Favorite Song",
                            dataType: "audio",
                            fileUrl: "https://example.com/song.mp3",
                            addedBy: "Alice",
                            addedDate: "2024-11-28T18:30:00.000Z"
                        },
                        {
                            id: "204",
                            name: "Birthday Video",
                            dataType: "video",
                            fileUrl: "https://example.com/video.mp4",
                            addedBy: "Bob",
                            addedDate: "2024-12-02T20:45:00.000Z"
                        }
                    ],
                    capsuleSize: 10,
                }
            ],
        }).as('getCapsules');


        cy.visit('http://localhost:3000');
        cy.contains('Přihlásit').click();
        cy.get('input[placeholder="vase@email.cz"]').type('test@example.com');
        cy.get('input[placeholder="••••••••"]').type('password123');
        cy.contains('Přihlásit se').click();
        cy.wait('@login');
        cy.url().should('eq', 'http://localhost:3000/dashboard');
    });

    it('should display the dashboard statistics and capsules', () => {
        // Wait for capsules to load
        cy.wait('@getCapsules');

        // Check statistics
        cy.contains('Celkem kapslí').should('be.visible');
        cy.contains('1').should('be.visible'); // Total capsules
        cy.contains('Čeká na otevření').should('be.visible');
        cy.contains('1').should('be.visible'); // Pending open
        // cy.contains('Sdíleno se mnou').should('be.visible');
        // cy.contains('1').should('be.visible'); // Shared with me
        //
        // // Check capsules list
        // cy.contains('Capsule 1').should('be.visible');
        // cy.contains('Capsule 2').should('be.visible');
        // cy.contains('Shared Capsule').should('be.visible');
    });

//     it('should filter capsules by search query', () => {
//         // Wait for capsules to load
//         cy.wait('@getCapsules');
//
//         // Search for a capsule
//         cy.get('input[placeholder="Hledat kapsle..."]').type('Capsule 1');
//         cy.contains('Capsule 1').should('be.visible');
//         cy.contains('Capsule 2').should('not.exist');
//         cy.contains('Shared Capsule').should('not.exist');
//     });
//
//     it('should filter capsules by status', () => {
//         // Wait for capsules to load
//         cy.wait('@getCapsules');
//
//         // Filter by "Čekající"
//         cy.get('select').select('Čekající');
//         cy.contains('Shared Capsule').should('be.visible');
//         cy.contains('Capsule 1').should('not.exist');
//         cy.contains('Capsule 2').should('not.exist');
//
//         // Filter by "Uzamčené"
//         cy.get('select').select('Uzamčené');
//         cy.contains('Capsule 2').should('be.visible');
//         cy.contains('Capsule 1').should('not.exist');
//         cy.contains('Shared Capsule').should('not.exist');
//     });
//
//     it('should display a message when no capsules match the filters', () => {
//         // Wait for capsules to load
//         cy.wait('@getCapsules');
//
//         // Apply a filter that doesn't match any capsules
//         cy.get('input[placeholder="Hledat kapsle..."]').type('Nonexistent');
//         cy.contains('Zatím nemáte žádné kapsle :(').should('be.visible');
//     });
//
//     it('should navigate to capsule detail page when clicking "Zobrazit detail"', () => {
//         // Wait for capsules to load
//         cy.wait('@getCapsules');
//
//         // Click on "Zobrazit detail" for Capsule 1
//         cy.contains('Capsule 1')
//             .parent()
//             .find('button')
//             .contains('Zobrazit detail')
//             .click();
//
//         // Assert navigation to capsule detail page
//         cy.url().should('include', '/capsuleDetail/1');
//     });
//
//     it('should allow creating a new capsule', () => {
//         // Click on "Nová kapsle"
//         cy.contains('Nová kapsle').click();
//
//         // Assert navigation to create capsule page
//         cy.url().should('include', '/createCapsule');
//     });
});
