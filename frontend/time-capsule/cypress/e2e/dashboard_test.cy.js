describe('Dashboard Page Tests', () => {
    beforeEach(() => {
        cy.intercept('POST', '/user/login', {
            statusCode: 200,
            body: {
                message: 'Login successful',
                email: 'test@example.com',
                token: 'mockedToken',
            },
        }).as('login');

        cy.intercept('GET', '/user/profile', {
            statusCode: 200,
            body: {
                "email": "aaaaaa@a.com",
                "name": "User",
                "bio": "This is bio of User",
                "role": "ROLE_REGISTERED"
            },
        }).as('user');

        cy.intercept('GET', '/capsules/contributor-capsules', {
            statusCode: 200,
            body: [],
        }).as('contributor');

        cy.intercept('POST', '/capsules/user', {
            statusCode: 200,
            body: [
                {
                    id: "1",
                    name: "Time Capsule 2025",
                    description: "A capsule containing memories and files for the future.",
                    createdDate: "2024-12-31T10:00:00.000Z",
                    unlockTime: "2025-12-31T10:00:00.000Z",
                    state: "EDIT",
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
                },
                {
                    id: "2",
                    name: "Time Capsule 2026",
                    description: "blabla",
                    createdDate: "2024-3-12T10:00:00.000Z",
                    unlockTime: "2026-12-31T10:00:00.000Z",
                    state: "WAIT",
                    users: [
                        { id: "101", name: "Alice", email: "alice@example.com" },
                        { id: "102", name: "Bob", email: "bob@example.com" },
                        { id: "103", name: "Jan", email: "example@example.com" },
                        { id: "104", name: "James", email: "example@example.com" },
                        { id: "105", name: "Beast", email: "example@example.com" }
                    ],
                    content: [],
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
        cy.wait('@getCapsules');
        cy.wait('@user');
        cy.wait('@contributor');

        cy.contains('Celkem kapslí').should('be.visible');
        cy.contains('1').should('be.visible');
        cy.contains('Čeká na otevření').should('be.visible');
        cy.contains('1').should('be.visible');
        cy.contains('Sdíleno se mnou').should('be.visible');
        cy.contains('1').should('be.visible');

        cy.contains('Time Capsule 2025').should('be.visible');
        cy.contains('Otevření: 31/12/2025').should('be.visible');
        cy.contains('2 přispěvatelů').should('be.visible');

        cy.contains('Zobrazit detail').click();
        cy.url().should('include', '/capsuleDetail/1');


    });

    it('should filter capsules by search query', () => {
        cy.wait('@getCapsules');

        cy.get('input[placeholder="Hledat kapsle..."]').type('Time Capsule 2025');
        cy.contains('Time Capsule 2025').should('be.visible');
        cy.contains('Otevření: 31/12/2025').should('be.visible');
        cy.contains('2 přispěvatelů').should('be.visible');

        cy.contains('Zobrazit detail').click();
        cy.url().should('include', '/capsuleDetail/1');
    });
//
    it('should filter capsules by status', () => {
        cy.wait('@getCapsules');

        cy.get('select').select('Čekající');
        cy.contains('Time Capsule 2025').should('be.visible');
        cy.contains('Otevření: 31/12/2025').should('be.visible');
        cy.contains('2 přispěvatelů').should('be.visible');

        cy.get('select').select('Uzamčené');
        cy.contains('Time Capsule 2026').should('be.visible');
        cy.contains('Otevření: 31/12/2026').should('be.visible');
        cy.contains('5 přispěvatelů').should('be.visible');
    });
});
