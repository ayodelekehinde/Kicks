package io.github.kicks.presentation

import io.github.kicks.data.Audio

class HomeViewModel {


     fun getAudios(): List<Audio>{
       return listOf(
           Audio(
               streamUrl = "https://cdn.pixabay.com/download/audio/2022/12/13/audio_a7eaf8e68b.mp3",
               imageUrl = "1.jpeg",
               artists = "Setze",
               title = "Thinkin About You (Radio Edit)",
               duration = "2:51",
           ),
           Audio(
               streamUrl = "https://cdn.pixabay.com/download/audio/2022/04/29/audio_b41f9553b2.mp3",
               imageUrl = "2.jpeg",
               artists = "Leonell Cassio",
               title = "Stuck In A Dream",
               duration = "4:58",
           ),
           Audio(
               streamUrl = "https://cdn.pixabay.com/download/audio/2022/01/26/audio_2694da1938.mp3",
               imageUrl = "3.png",
               artists = "Gvidon",
               title = "Success Starts With A Dream",
               duration = "2:01",
           ),
           Audio(
               streamUrl = "https://naijaloaded.store/assets/uploads/Asake-2-30.mp3",
               imageUrl = "4.png",
               artists = "Asake",
               title = "2:30",
               duration = "2:18",
           ),
           Audio(
               streamUrl = "https://naijaloaded.store/wp-content/uploads/2022/02/Rema-%E2%80%93-Calm-Down.mp3",
               imageUrl = "5.png",
               artists = "Rema",
               title = "Calm down",
               duration = "3:39",
           ),
           Audio(
               streamUrl = "https://cdn.pixabay.com/download/audio/2023/04/12/audio_6922b1de38.mp3",
               imageUrl = "6.jpeg",
               artists = "Matthew Mark",
               title = "Happy Tears",
               duration = "1:59",
           ),
           Audio(
               streamUrl = "https://cdn.pixabay.com/download/audio/2022/03/23/audio_41f668f967.mp3",
               imageUrl = "7.png",
               artists = "Nesrality",
               title = "Irish Folklore",
               duration = "2:27",
           ),
           Audio(
               streamUrl = "https://cdn.pixabay.com/download/audio/2022/03/23/audio_41f668f967.mp3",
               imageUrl = "8.jpeg",
               artists = "Norished",
               title = "Irish Sitar",
               duration = "2:07",
           ),
           Audio(
               streamUrl = "https://cdn.pixabay.com/download/audio/2022/11/06/audio_c1e6eebf61.mp3",
               imageUrl = "9.jpeg",
               artists = "SoulProd",
               title = "Game",
               duration = "2:10",
           ),
           Audio(
               streamUrl = "https://cdn.pixabay.com/download/audio/2022/12/28/audio_e232e79ed8.mp3",
               imageUrl = "10.jpeg",
               artists = "Roma Records",
               title = "Against the stream",
               duration = "2:44",
           ),

       )
    }
}
