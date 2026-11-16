import com.zhuofan.core.datasource.JdbcDao;
import com.zhuofan.core.common.CommonFunc;
import com.zhuofan.core.common.CommonObject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.zhuofan.core.eval.Eval;
import com.zhuofan.core.orm.model.*;
import com.zhuofan.core.orm.dao.*;
public class C_d8a754967d059d5040d405d683c0a344
{
    public Object getEvalResult(HashMap mapRow) throws Exception
    {
        return CommonObject.mapYesNo.get(mapRow.get("ISHIDE"));

    }
}
