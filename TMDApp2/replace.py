import sys

filepath = r'd:\projects\AndroidStudioProjects\TMDApp2\app\src\main\java\com\example\tmdapp\ui\screens\ExerciseScreen.kt'
with open(filepath, 'r', encoding='utf-8') as f:
    lines = f.readlines()

start_idx = -1
end_idx = -1
for i, line in enumerate(lines):
    if line.startswith('@Composable') and 'fun LocalVideoPlayer(videoResId: Int)' in lines[i+1]:
        start_idx = i
    if start_idx != -1 and line.startswith('// ─────────────────────────────────────────────') and 'Step Guide Tab' in lines[i+1]:
        end_idx = i - 1
        break

if start_idx != -1 and end_idx != -1:
    new_func = """@Composable
fun LocalVideoPlayer(videoResId: Int) {
    val context = LocalContext.current
    
    var isPlaying by remember { mutableStateOf(false) }
    var isFullscreen by remember { mutableStateOf(false) }
    
    // The player instance - created immediately when the composable enters
    val exoPlayer = remember(videoResId) {
        androidx.media3.exoplayer.ExoPlayer.Builder(context).build().apply {
            val uri = "android.resource://${context.packageName}/$videoResId".androidx.core.net.toUri()
            setMediaItem(androidx.media3.common.MediaItem.fromUri(uri))
            repeatMode = androidx.media3.common.Player.REPEAT_MODE_ONE
            volume = 0f // Muted initially
            prepare()
            playWhenReady = false
        }
    }
    
    // Cleanup player when composable is disposed or video changes
    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.release()
        }
    }

    // Effect to track playing state
    LaunchedEffect(exoPlayer) {
        val listener = object : androidx.media3.common.Player.Listener {
            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }
        }
        exoPlayer.addListener(listener)
        isPlaying = exoPlayer.isPlaying
    }

    Column {
        // Video Preview / Player Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Black)
                .clickable {
                    if (!isPlaying) {
                        exoPlayer.volume = 1f
                        exoPlayer.play()
                    } else {
                        exoPlayer.pause()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        player = exoPlayer
                        useController = false 
                        setBackgroundColor(android.graphics.Color.BLACK)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
            
            // Center play icon overlay for professional appearance
            if (!isPlaying) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.PlayCircle,
                        contentDescription = "Play Video",
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(64.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Custom Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Play/Pause Button
            Button(
                onClick = {
                    if (isPlaying) {
                        exoPlayer.pause()
                    } else {
                        exoPlayer.volume = 1f
                        exoPlayer.play()
                    }
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isPlaying) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play"
                )
                Spacer(Modifier.width(8.dp))
                Text(text = if (isPlaying) "Pause" else "Play")
            }

            // Stop Button (Fully stops playback, resets video properly, prevents background audio)
            OutlinedButton(
                onClick = {
                    exoPlayer.pause()
                    exoPlayer.seekTo(0)
                    exoPlayer.volume = 0f
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Stop, contentDescription = "Stop")
                Spacer(Modifier.width(8.dp))
                Text("Stop")
            }

            // Fullscreen Button
            IconButton(
                onClick = { isFullscreen = true },
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Icon(
                    Icons.Default.Fullscreen, 
                    contentDescription = "Fullscreen",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    // Fullscreen Dialog
    if (isFullscreen) {
        Dialog(
            onDismissRequest = { isFullscreen = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnBackPress = true,
                decorFitsSystemWindows = false
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                AndroidView(
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            player = exoPlayer
                            useController = false 
                            setBackgroundColor(android.graphics.Color.BLACK)
                        }
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            if (isPlaying) exoPlayer.pause() else {
                                exoPlayer.volume = 1f
                                exoPlayer.play()
                            }
                        }
                )

                if (!isPlaying) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f))
                            .clickable {
                                exoPlayer.volume = 1f
                                exoPlayer.play()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.PlayCircle,
                            contentDescription = "Play Video",
                            tint = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.size(80.dp)
                        )
                    }
                }

                // Fullscreen Overlay Controls
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 32.dp)
                        .fillMaxWidth(0.8f)
                        .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            if (isPlaying) {
                                exoPlayer.pause()
                            } else {
                                exoPlayer.volume = 1f
                                exoPlayer.play()
                            }
                        }
                    ) {
                        Icon(
                            if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Play/Pause",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            exoPlayer.pause()
                            exoPlayer.seekTo(0)
                            exoPlayer.volume = 0f
                        }
                    ) {
                        Icon(
                            Icons.Default.Stop,
                            contentDescription = "Stop",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    IconButton(
                        onClick = { isFullscreen = false }
                    ) {
                        Icon(
                            Icons.Default.FullscreenExit,
                            contentDescription = "Exit Fullscreen",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }
        }
    }
}
"""
    new_lines = lines[:start_idx] + [new_func] + ['\n'] + lines[end_idx:]
    with open(filepath, 'w', encoding='utf-8') as f:
        f.writelines(new_lines)
    print(f'Successfully replaced from line {start_idx} to {end_idx}')
else:
    print(f'Failed to find bounds: start={start_idx}, end={end_idx}')
