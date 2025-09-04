package com.stickercreator.app.data.repository

import android.content.Context
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertNotNull
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class ImageRepositoryTest {

    private lateinit var imageRepository: ImageRepository

    @Mock
    private lateinit var mockContext: Context

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        imageRepository = ImageRepository(mockContext)
    }

    @Test
    fun `repository should be initialized correctly`() {
        assertNotNull(imageRepository)
    }
}
