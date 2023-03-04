package com.example.mobilvizeproje


import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.*
import android.text.method.LinkMovementMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.mobilvizeproje.databinding.FragmentDetailBinding
import java.util.concurrent.Executors

private lateinit var game: Result
private val MAX_LINES = 4
@SuppressLint("StaticFieldLeak")
private lateinit var detailTextView: TextView
@SuppressLint("StaticFieldLeak")
private lateinit var readMoreView: TextView

class DetailFragment : Fragment() {
    private lateinit var binding: FragmentDetailBinding
    private lateinit var viewModel: MainViewModel  //view model degiskeni olusturuldu

    // değişkenler oluşturuldu
    var image: String? = null
    var name: String? = null
    var detailTxt : String? = null
    var score : Int? = null
    var type : ArrayList<String>? = null
    var favourite : Boolean? = null
    var id : Int? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //view model providerı ile initilaze edildi
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailBinding.inflate(layoutInflater)

        // game fragmentdan gelen datalar degiskenlere atandı
        image = requireArguments().getString("game_image")
        name = requireArguments().getString("game_name")
        //detailTxt = requireArguments().getString("game_detail")
        detailTxt = "The game is simply dummy text of the printing and " +
                "typesetting industry. Lorem Ipsum has been the industry's standard dummy " +
                "text ever since the 1500s, when an unknown printer took a galley of type " +
                "and scrambled it to make a type specimen book. It has survived not only " +
                "five centuries, but also the leap into electronic typesetting, remaining " +
                "essentially unchanged. It was popularised in the 1960s with the release of " +
                "Letraset sheets containing Lorem Ipsum passages, and more recently with " +
                "desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum."
        score = requireArguments().getInt("game_score")
        type = requireArguments().getStringArrayList("game_type")
        favourite = requireArguments().getBoolean("game_favourite")
        id = requireArguments().getInt("game_id")

        //fragmentanın ilgili alanlarına gelen veriler atandı

        // Declaring executor to parse the URL
        val executor = Executors.newSingleThreadExecutor()

        // Once the executor parses the URL
        // and receives the image, handler will load it
        // in the ImageView
        val handler = Handler(Looper.getMainLooper())

        // Initializing the image
        var imageB: Bitmap?

        // Only for Background process (can take time depending on the Internet speed)
        executor.execute {

            // Tries to get the image and post it in the ImageView
            // with the help of Handler
            try {
                val `in` = java.net.URL(image).openStream()
                imageB = BitmapFactory.decodeStream(`in`)

                // Only for making changes in UI
                handler.post {
                    binding.detailImage.setImageBitmap(imageB)
                }
            }

            // If the URL doesnot point to
            // image or any other kind of failure
            catch (e: Exception) {
                e.printStackTrace()
            }
        }

        binding.detailName.text = name

        detailTextView = binding.description
        detailTextView.text=detailTxt
        detailTextView.maxLines = MAX_LINES

        readMoreView = binding.readMore

        readMoreView.setOnClickListener {
            if (readMoreView.text == "Read More"){
                detailTextView.maxLines = Int.MAX_VALUE
                binding.readMore.text = "Show Less"
            }else{
                detailTextView.maxLines = MAX_LINES
                binding.readMore.text = "Read More"
            }
        }

        val visitReddit = binding.visitReddit
        visitReddit.movementMethod = LinkMovementMethod.getInstance()
        binding.visitWebsite.movementMethod = LinkMovementMethod.getInstance()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val typeGenre = ArrayList<Genre>()
        var genre:Genre

        for (name in type!!){
            genre = Genre(name)
            typeGenre.add(genre)
        }

        //navigasyonla gelen verilerle game nesnesi olusturuldu
        game = Result(
            image.toString(),
            typeGenre,
            id,
            score,
            name.toString(),
            favourite,
            detailTxt.toString(),
        )


        for(oyun in viewModel.favouriteGameslist.value!!){
            if (oyun.name == game.name)
                binding.addFavourite.text = "Favourited"
            else
                binding.addFavourite.text = "Favourite"
        }

        //games ekranına geçiş yapılır
        binding.backToGames.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_detailFragment_to_homeFragment)
        }

        //favuorite yazısına tıklanınca nesne favoriler listesine ekleniyor
        binding.addFavourite.setOnClickListener {
            //game.favourite = true
            viewModel.favouriteGameslist.observe(viewLifecycleOwner, Observer {
                viewModel.addFav(game)
                //viewModel.changeFavourite(game)  // game in favourite parametresi view model üzerinde true ya set ediliyor
                binding.addFavourite.text = "Favourited"  // sayfadaki favoutire yazısı favourited a set ediliyor

            })

        }
    }

}